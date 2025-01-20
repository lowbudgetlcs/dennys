package com.lowbudgetlcs

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.models.fetchTeamId
import com.lowbudgetlcs.repositories.AndCriteria
import com.lowbudgetlcs.repositories.games.GameRepositoryImpl
import com.lowbudgetlcs.repositories.games.SeriesCriteria
import com.lowbudgetlcs.repositories.games.ShortcodeCriteria
import com.lowbudgetlcs.repositories.games.TeamWinCriteria
import com.lowbudgetlcs.repositories.series.SeriesRepositoryImpl
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class TournamentEngine : Worker {
    private val queue = "CALLBACK"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.TournamentEngine")
    private val db = LblcsDatabaseBridge().db
    private val riot = RiotBridge()
    private val gamesR = GameRepositoryImpl()
    private val seriesR = SeriesRepositoryImpl()

    private fun main() {
        logger.info("TournamentEngine starting...")
        val messageq = RabbitMQBridge(queue)
        logger.debug("Listening on $queue...")
        val readRiotCallback = DeliverCallback { _, delivery: Delivery ->
            val message = String(delivery.body, charset("UTF-8"))
            logger.debug("[x] Recieved Message: {}", message)
            try {
                val callback = Json.decodeFromString<RiotCallback>(message)
                riot.match(callback.gameId)?.let { match ->
                    db.transaction {
                        // Update game row with game outcome
                        gamesR.readByCriteria(ShortcodeCriteria(callback.shortCode)).firstOrNull()?.let { game ->
                            val winner = fetchTeamId(match.participants.filter { it.didWin() })
                            val loser = fetchTeamId(match.participants.filter { !it.didWin() })
                            if (winner == null || loser == null) throw Throwable("Invalid teamId.") // Missing team IDs
                            gamesR.update(
                                game.copy(
                                    winner = winner, loser = loser, callbackResult = callback
                                )
                            )
                            // Check if series needs updating
                            val (team1, team2) = Pair(winner, loser)
                            seriesR.readById(game.series)?.let { series ->
                                // Magic number yayyyy! Playoff games aren't best of 5, I need to do this better
                                val winCondition = if (series.playoffs) 3 else 2

                                val team1Wins = gamesR.readByCriteria(
                                    AndCriteria(TeamWinCriteria(team1), SeriesCriteria(game.series))
                                ).size
                                val team2Wins = gamesR.readByCriteria(
                                    AndCriteria(TeamWinCriteria(team2), SeriesCriteria(game.series))
                                ).size
                                when (winCondition) {
                                    team1Wins -> seriesR.update(series.copy(winner = team1, loser = team2))
                                    team2Wins -> seriesR.update(series.copy(loser = team1, winner = team2))
                                    // Otherwise, series has not concluded
                                }
                            }
                        }
                    }
                }
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
        messageq.listen(readRiotCallback)
    }

    override fun start() {
        this.main()
    }
}
