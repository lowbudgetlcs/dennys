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
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class TournamentEngine private constructor(
    override val queue: String,
    private val gamesR: IGameRepository,
    private val seriesR: ISeriesRepository,
    private val playersR: IPlayerRepository
) : AbstractWorker(), RabbitMQWorker {
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.workers.TournamentEngine")
    private val messageq = RabbitMQBridge(queue)
    private val db = LblcsDatabaseBridge().db
    private val riot = RiotBridge()

    companion object {
        fun createInstance(queue: String): TournamentEngine =
            TournamentEngine(queue, AllGamesLBLCS(), AllSeriesLBLCS(), AllPlayersLBLCS())
    }

    override fun createInstance(instanceId: Int): AbstractWorker = Companion.createInstance(queue)

    override fun start() {
        logger.info("TournamentEngine starting...")
        logger.debug("Listening on $queue...")
        messageq.listen { _, delivery ->
            processMessage(delivery)
        }
    }

    override fun processMessage(delivery: Delivery) {
        val message = String(delivery.body, charset("UTF-8"))
        logger.debug("[x] Recieved Message: {}", message)
        try {
            val callback = Json.decodeFromString<RiotCallback>(message)
            processRiotCallback(callback)
            messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
        } catch (e: SerializationException) {
            // Generally is an application error- we do not want to lose the message
            logger.error("[x] Error while decoding message: {}", message)
            logger.error(e.message)
        } catch (e: IllegalArgumentException) {
            logger.warn("[x] Message was not valid Riot Callback: {}.", message)
            // Delete invalid messages
            messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
        }
    }

    private fun processRiotCallback(callback: RiotCallback) {
        riot.match(callback.gameId)?.let { match ->
            try {
                // We throw an exception to cancel the database transaction.
                db.transaction {
                    val (winner, loser) = match.participants.partition { it.didWin() }.let { (winners, losers) ->
                        playersR.fetchTeamId(winners) to playersR.fetchTeamId(losers)
                    }
                    if (winner == null || loser == null) throw IllegalArgumentException("TeamId not found.")
                    updateGame(callback, winner, loser)?.let {
                        updateSeries(
                            it, winner, loser
                        )
                    }
                }
            } catch (e: IllegalArgumentException) {
                logger.warn("Could not fetch one or both TeamIds.")
            }
        }
    }

    private fun updateGame(callback: RiotCallback, winner: TeamId, loser: TeamId): Game? {
        gamesR.readByCriteria(ShortcodeCriteria(callback.shortCode)).first().let { game ->
            return gamesR.update(
                game.copy(
                    winner = winner, loser = loser, callbackResult = callback
                )
            )
        }
    }

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

            fun logWin(team: TeamId) = logger.debug("Team {} wins series {}.", team, series.id)
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
                    logger.debug("Series {} has not concluded.", series.id)
                }
            }
        }
    }
}
