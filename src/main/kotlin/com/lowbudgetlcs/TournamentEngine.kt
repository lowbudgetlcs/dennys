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
        listen(messageq, riot)
    }

    private fun listen(messageq: RabbitMQBridge, riot: RiotBridge) {
        logger.debug("Listening on $queue...")
        val readRiotCallback = DeliverCallback { _, delivery: Delivery ->
            val message = String(delivery.body, charset("UTF-8"))
            logger.debug("[x] Recieved Message: {}", message)
            messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
            try {
                val callback = Json.decodeFromString<RiotCallback>(message)
                // Update game row with game outcome
                val match = riot.match(callback.gameId)
                val (winner: Int, loser: Int) = Pair(
                    getTeamId(match.participants.filter { participant -> participant.didWin() }),
                    getTeamId(match.participants.filter { participant -> !participant.didWin() }),
                )
                lblcs.gamesQueries.updateGameOutcome(
                    winner, loser, callback.shortCode
                )
                // Check if series is complete
                //   If complete, update series
            } catch (e: SerializationException) {
                logger.error("[x] Error while decoding message: {}", message)
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