package com.lowbudgetlcs

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.repositories.games.GameRepositoryImpl
import com.lowbudgetlcs.repositories.players.PlayerRepositoryImpl
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class StatDaemon {
    private val queue = "STATS"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.StatDaemon")
    private val db = LblcsDatabaseBridge().db
    private val games = GameRepositoryImpl()
    private val players = PlayerRepositoryImpl()

    fun main() {
        logger.info("StatDaemon running...")
        val messageq = RabbitMQBridge(queue)
        val riot = RiotBridge()
        logger.debug("Listening on $queue...")
        val readRiotCallback = DeliverCallback { _, delivery: Delivery ->
            val message = String(delivery.body, charset("UTF-8"))
            logger.debug("[x] Recieved Message: {}", message)
            try {
                val callback = Json.decodeFromString<RiotCallback>(message)
                riot.match(callback.gameId)?.let { match ->
                    games.readByShortcode(callback.shortCode)?.let { game ->
                        // Process Team data first to cause errors as early as possible
//                        lblcs.gameDumpsQueries.dump(game.id, Json.encodeToString<LOLMatch>(match))
//                        for (team in match.teams) {
//
//                        }
                        for (player in match.participants) {
                            logger.debug("Processing player data for code '{}'.", callback.shortCode)
                            try {
                                db.transaction {
                                    players.createPerformance(player.puuid, game.id)?.let { performanceId ->
                                        players.createGameData(
                                            performanceId,
                                            player.kills,
                                            player.deaths,
                                            player.assists,
                                            player.championLevel,
                                            player.goldEarned.toLong(),
                                            player.visionScore.toLong(),
                                            player.totalDamageDealtToChampions.toLong(),
                                            player.totalHealsOnTeammates.toLong(),
                                            player.totalDamageShieldedOnTeammates.toLong(),
                                            player.totalDamageTaken.toLong(),
                                            player.damageSelfMitigated.toLong(),
                                            player.damageDealtToTurrets.toLong(),
                                            player.longestTimeSpentLiving.toLong(),
                                            player.doubleKills.toShort(),
                                            player.tripleKills.toShort(),
                                            player.quadraKills.toShort(),
                                            player.pentaKills.toShort(),
                                            player.totalMinionsKilled + player.neutralMinionsKilled,
                                            player.championName,
                                            player.item0,
                                            player.item1,
                                            player.item2,
                                            player.item3,
                                            player.item4,
                                            player.item5,
                                            player.item6,
                                            player.perks.perkStyles[0].style,
                                            player.perks.perkStyles[1].style,
                                            player.summoner1Id,
                                            player.summoner2Id
                                        )
                                    }
                                }
                                logger.debug(
                                    "Successfully processed player '{}#{}' ({})",
                                    player.riotIdName,
                                    player.riotIdTagline,
                                    callback.shortCode
                                )
                            } catch (e: Throwable) {
                                logger.error(
                                    "Transaction failed for '{}' ({}')",
                                    "${player.riotIdName}#${player.riotIdTagline}",
                                    callback.shortCode
                                )
                                logger.error(e.message)
                            }
                        }
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
}