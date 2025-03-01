package com.lowbudgetlcs.workers

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.TeamId
import com.lowbudgetlcs.repositories.AndCriteria
import com.lowbudgetlcs.repositories.games.*
import com.lowbudgetlcs.repositories.players.AllPlayersLBLCS
import com.lowbudgetlcs.repositories.players.IPlayerRepository
import com.lowbudgetlcs.repositories.series.AllSeriesLBLCS
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.Delivery
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This service worker consumes [RiotCallback]s off of [queue] and saves
 * the result of the finished [Game]. It also checks if the [Series] owning
 * [Game] is complete. If it is, it saves the result.
 */
class TournamentEngine private constructor(
    override val queue: String,
    private val gamesR: IGameRepository,
    private val seriesR: ISeriesRepository,
    private val playersR: IPlayerRepository
) : AbstractWorker(), IMessageQListener {
    private val logger : Logger = LoggerFactory.getLogger(TournamentEngine::class.java)
    private val messageq = RabbitMQBridge(queue)
    private val db = LblcsDatabaseBridge().db
    private val riot = RiotBridge()

    /**
     * Private constructor and companion object prevent direct instantiation.
     *
     * This behavior is deprecated and will be removed in future versions.
     */
    companion object {
        fun createInstance(queue: String): TournamentEngine =
            TournamentEngine(queue, AllGamesLBLCS(), AllSeriesLBLCS(), AllPlayersLBLCS())
    }

    override fun createInstance(instanceId: Int): AbstractWorker = Companion.createInstance(queue)

    override fun start() {
        logger.info("🚀 TournamentEngine starting...")
        logger.debug("📡 Listening on queue: `$queue`")

        messageq.listen { _, delivery ->
            processMessage(delivery)
        }
    }

    /**
     * Consumes [delivery] from [queue] and parses it as a [RiotCallback].
     * Begins game and series processing.
     */
    override fun processMessage(delivery: Delivery) {
        val message = String(delivery.body, charset("UTF-8"))
        logger.info("📩 Received message: $message")
        try {
            val callback = Json.decodeFromString<RiotCallback>(message)
            logger.info("✅ Successfully decoded RiotCallback for game ID: ${callback.gameId}")
            processRiotCallback(callback)
            messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
        } catch (e: SerializationException) {
            // Generally is an application error- we do not want to lose the message
            logger.error("❌ Failed to decode message: $message", e)
        } catch (e: IllegalArgumentException) {
            logger.warn("🚫 Invalid Riot Callback message: $message.")
            // Delete invalid messages
            messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
        }
    }

    /**
     * Fetches a match from the RiotAPI and process the winning and losing teams. Then, saves
     * the winner, loser, and result of the game. If the series owning said game is complete,
     * save the winner and loser of the series.
     */
    private fun processRiotCallback(callback: RiotCallback) {
        logger.info("🔍 Fetching match details for game ID: ${callback.gameId}")

        riot.match(callback.gameId)?.let { match ->
            try {
                // We throw an exception to cancel the database transaction.
                db.transaction {
                    val (winner, loser) = match.participants.partition { it.didWin() }.let { (winners, losers) ->
                        playersR.fetchTeamId(winners) to playersR.fetchTeamId(losers)
                    }
                    if (winner == null || loser == null) throw IllegalArgumentException("TeamId not found.")

                    logger.info("🏆 Winner: $winner, ❌ Loser: $loser")

                    updateGame(callback, winner, loser)?.let {
                        updateSeries(
                            it, winner, loser
                        )
                    }
                }
            } catch (e: IllegalArgumentException) {
                logger.warn("⚠️ Could not fetch one or both TeamIds.")
            }
        }
    }

    /**
     * Updates the game in storage derived from [callback] with a [winner] and [loser].
     */
    private fun updateGame(callback: RiotCallback, winner: TeamId, loser: TeamId): Game? {
        logger.info("📝 Updating game record for shortcode: ${callback.shortCode}")

        gamesR.readByCriteria(ShortcodeCriteria(callback.shortCode)).first().let { game ->
            val updatedGame = game.copy(winner = winner, loser = loser, callbackResult = callback)
            return gamesR.update(updatedGame)
        }
    }

    /**
     * Updates the series in storage that owns [game] with a winner and loser.
     */
    private fun updateSeries(game: Game, team1: TeamId, team2: TeamId) {
        seriesR.readById(game.series)?.let { series ->
            // Magic number yayyyy! This needs an actual solution- for now the app only supports Bo3.
            val winCondition = 2
            val team1Wins = gamesR.readByCriteria(
                AndCriteria(TeamWinCriteria(team1), SeriesCriteria(game.series))
            ).size
            val team2Wins = gamesR.readByCriteria(
                AndCriteria(TeamWinCriteria(team2), SeriesCriteria(game.series))
            ).size

            fun logWin(team: TeamId) = logger.debug("🏅 Team $team wins series ${series.id}!")
            when (winCondition) {
                team1Wins -> {
                    seriesR.update(series.copy(winner = team1, loser = team2))
                    logWin(team1)
                }

                team2Wins -> {
                    seriesR.update(series.copy(loser = team1, winner = team2))
                    logWin(team2)
                }
                // Otherwise, series has not concluded
                else -> {
                    logger.info("⏳ Series ${series.id} is still ongoing...")
                }
            }
        }
    }
}
