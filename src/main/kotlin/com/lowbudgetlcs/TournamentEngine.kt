package com.lowbudgetlcs

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.repositories.games.GameRepositoryImpl
import com.lowbudgetlcs.repositories.players.PlayerRepositoryImpl
import com.lowbudgetlcs.repositories.series.SeriesRepositoryImpl
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant

class TournamentEngine {
    private val queue = "CALLBACK"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.TournamentEngine")
    private val db = LblcsDatabaseBridge.db
    private val riot = RiotBridge()
    private val gamesR = GameRepositoryImpl()
    private val playersR = PlayerRepositoryImpl()
    private val seriesR = SeriesRepositoryImpl()

    fun main() {
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
                        gamesR.readByShortcode(callback.shortCode)?.let { game ->
                            val winner = fetchTeamId(match.participants.filter { it.didWin() })
                            val loser = fetchTeamId(match.participants.filter { !it.didWin() })
                            if (winner == -1 || loser == -1) throw Throwable("Invalid teamId.") // Missing team IDs
                            if (gamesR.updateWinnerLoserCallbackById(
                                    winner, loser, Json.encodeToString<RiotCallback>(callback), game.id
                                )
                            ) {
                                logger.warn("No game ID returned- likely result was not recorded.")
                            } else {
                                logger.debug("Successfully updated code :'{}' outcome.", callback.shortCode)
                            }
                            // Check if series needs updating
                            seriesR.readById(game.series_id)?.let { series ->
                                // Magic number yayyyy! Playoff games aren't best of 5, I need to do this better
                                val winCondition = if (series.playoffs) 3 else 2
                                val (team1: Int, team2: Int) = Pair(winner, loser)
                                val team1Wins = gamesR.countTeamWinsBySeries(game.series_id, team1)
                                val team2Wins = gamesR.countTeamWinsBySeries(game.series_id, team2)
                                when (winCondition) {
                                    team1Wins -> seriesR.updateWinnerLoserById(team1, team2, series.id)
                                    team2Wins -> seriesR.updateWinnerLoserById(team1, team2, series.id)
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

    private fun fetchTeamId(participants: List<MatchParticipant>): Int {
        for (participant in participants) {
            playersR.readByPuuid(participant.puuid)?.let { player ->
                if (player.team_id != null) return player.team_id
            }
        }
        return -1
    }
}
