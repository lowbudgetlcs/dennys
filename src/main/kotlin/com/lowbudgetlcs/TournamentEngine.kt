package com.lowbudgetlcs

import com.lowbudgetlcs.bridges.DatabaseBridge
import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant

class TournamentEngine {
    private val queue = "CALLBACK"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.TournamentEngine")
    private val lblcs = DatabaseBridge().db

    fun main() {
        logger.info("TournamentEngine running...")
        val messageq = RabbitMQBridge(queue)
        val riot = RiotBridge()
        logger.debug("Listening on $queue...")
        val readRiotCallback = DeliverCallback { _, delivery: Delivery ->
            val message = String(delivery.body, charset("UTF-8"))
            logger.debug("[x] Recieved Message: {}", message)
            try {
                val callback = Json.decodeFromString<RiotCallback>(message)
                // Update game row with game outcome
                lblcs.gamesQueries.selectGameByShortcode(callback.shortCode).executeAsOneOrNull()?.let { game ->
                    val match = riot.match(callback.gameId)
                    // Write match data to STAT queue to be processed to prevent multiple Riot API requests?
                    val winner = getTeamId(match.participants.filter { participant -> participant.didWin() })
                    val loser = getTeamId(match.participants.filter { participant -> !participant.didWin() })
                    if (winner == -1 || loser == -1) return@DeliverCallback // Missing team IDs
                    when (lblcs.gamesQueries.setWinnerLoserById(
                        winner, loser, game.id
                    ).executeAsOneOrNull()) {
                        null -> logger.warn("No game ID returned- likely result was not recorded.")
                        else -> logger.debug("Successfully updated code :'{}' outcome.", callback.shortCode)
                    }
                    // Check if series needs updating
                    val series = lblcs.seriesQueries.selectById(game.series_id).executeAsOne()
                    val winCondition = if(series.playoffs) 2 else 3
                    val (team1: Int, team2: Int) = Pair(winner, loser)
                    val team1Wins = lblcs.gamesQueries.countWinsBySeries(game.series_id, team1).executeAsOneOrNull() ?: 0
                    val team2Wins = lblcs.gamesQueries.countWinsBySeries(game.series_id, team2).executeAsOneOrNull() ?: 0
                    when(winCondition) {
                        team1Wins.toInt() -> lblcs.seriesQueries.setWinner(team1, series.id)
                        team2Wins.toInt() -> lblcs.seriesQueries.setWinner(team2, series.id)
                        // Otherwise, series has not concluded
                    }
                }
                messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
            } catch (e: SerializationException) {
                logger.error("[x] Error while decoding message: {}", message)
                logger.error(e.message)
            } catch (e: IllegalArgumentException) {
                logger.warn("[x] Message was not valid Riot Callback: {}.", message)
            }
        }
        messageq.listen(readRiotCallback)
    }


    // Given a list of MatchParticipants, fetch the team id from the database
    // I am nearly positive there is a way to do this in a single query rather than (potentially) 5.
    private fun getTeamId(players: List<MatchParticipant>): Int {
        for (player in players) {
            lblcs.playersQueries.selectTeamId(player.puuid).executeAsOneOrNull()?.let {
                it.team_id?.let { teamId ->
                    return teamId
                }
            }
        }
        logger.warn("No players in match exist in database.")
        return -1
    }
}