package com.lowbudgetlcs.workers

import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.IGameRepository
import com.lowbudgetlcs.repositories.IMatchRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.ISeriesRepository
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.Delivery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This service worker consumes [RiotCallback]s off of [queue] and saves
 * the result of the finished [Game]. It also checks if the [Series] owning
 * [Game] is complete. If it is, it saves the result.
 */
class TournamentEngine(
    override val queue: String,
    private val gamesRepository: IGameRepository,
    private val seriesRepository: ISeriesRepository,
    private val playersRepository: IPlayerRepository,
    private val matchRepository: IMatchRepository
) : IMessageQueueListener {

    private val logger: Logger = LoggerFactory.getLogger(TournamentEngine::class.java)
    private val messageQueue = RabbitMQBridge(queue)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun start() {
        logger.info("🚀 TournamentEngine starting...")
        logger.debug("📡 Listening on queue: `$queue`")
        messageQueue.listen { _, delivery ->
            processMessage(delivery)
        }
    }

    /**
     * Consumes [delivery] from [queue] and parses it as a [RiotCallback].
     * Begins game and series processing.
     */
    override fun processMessage(delivery: Delivery) {
        val message = String(delivery.body, charset("UTF-8"))
        logger.info("📩 TournamentEngine received message!")
        try {
            val callback = Json.decodeFromString<RiotCallback>(message)
            logger.debug("✅ Successfully decoded RiotCallback for game ID: ${callback.gameId}")
            scope.launch {
                processRiotCallback(callback)
            }
            messageQueue.channel.basicAck(delivery.envelope.deliveryTag, false)
        } catch (e: SerializationException) {
            // Generally is an application error- we do not want to lose the message
            logger.error("❌ Failed to decode message: $message", e)
        } catch (e: IllegalArgumentException) {
            logger.warn("🚫 Invalid Riot Callback message: $message.")
            // Delete invalid messages
            messageQueue.channel.basicAck(delivery.envelope.deliveryTag, false)
        }
    }

    /**
     * Fetches a match from the RiotAPI and process the winning and losing teams. Then, saves
     * the winner, loser, and result of the game. If the series owning said game is complete,
     * save the winner and loser of the series.
     */
    private suspend fun processRiotCallback(callback: RiotCallback) {
        logger.info("🔍 Fetching match details for game ID: ${callback.gameId}...")

        val tournamentMatch = matchRepository.getMatch(callback.gameId)
        tournamentMatch?.let { match ->
            try {
                db.transaction {
                    val (winner, loser) = match.matchInfo.participants.partition { it.win }.let { (winners, losers) ->
                        playersRepository.fetchTeamId(winners) to playersRepository.fetchTeamId(losers)
                    }

                    if (winner == null || loser == null) throw IllegalArgumentException("TeamId not found.")

                    logger.debug("\uD83C\uDFC6 Winner: {}, ❌ Loser: {}", winner, loser)

                    updateGame(callback, winner, loser)?.let {
                        updateSeries(
                            it, winner, loser
                        )
                    }
                }
            } catch (e: IllegalArgumentException) {
                logger.warn("⚠️ Could not fetch one or both TeamIds.")
            }
            logger.info("✅ Finished processing match `${match.matchInfo.gameId}` for shortcode `${callback.shortCode}`")
        }
    }

    /**
     * Updates the game in storage derived from [callback] with a [winner] and [loser].
     */
    private fun updateGame(callback: RiotCallback, winner: TeamId, loser: TeamId): Game? {
        logger.info("📝 Updating game record for shortcode: ${callback.shortCode}...")

        gamesRepository.readByCriteria(ShortcodeCriteria(callback.shortCode)).first().let { game ->
            val updatedGame = game.copy(winner = winner, loser = loser, callbackResult = callback)
            return gamesRepository.update(updatedGame)
        }
    }

    /**
     * Updates the series in storage that owns [game] with a winner and loser.
     */
    private fun updateSeries(game: Game, team1: TeamId, team2: TeamId) {
        logger.info("📝 Updating series record for derived from: ${game.id}...")
        seriesRepository.readById(game.series)?.let { series ->
            logger.debug("\uD83D\uDCDD Updating series record: {}...", series.id)
            // Magic number yayyyy! This needs an actual solution- for now the app only supports Bo3.
            val winCondition = 2
            val team1Wins = gamesRepository.readByCriteria(
                AndCriteria(TeamWinCriteria(team1), SeriesCriteria(game.series))
            ).size
            val team2Wins = gamesRepository.readByCriteria(
                AndCriteria(TeamWinCriteria(team2), SeriesCriteria(game.series))
            ).size

            fun logWin(team: TeamId) = logger.debug("\uD83C\uDFC5 Team {} wins series {}!", team, series.id)
            when (winCondition) {
                team1Wins -> {
                    seriesRepository.update(series.copy(winner = team1, loser = team2))
                    logWin(team1)
                }

                team2Wins -> {
                    seriesRepository.update(series.copy(loser = team1, winner = team2))
                    logWin(team2)
                }
                // Otherwise, series has not concluded
                else -> {
                    logger.debug("⏳ Series {} is still ongoing...", series.id)
                }
            }
        }
    }
}
