package com.lowbudgetlcs

import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.PlayerGameData
import com.lowbudgetlcs.models.fetchTeamId
import com.lowbudgetlcs.repositories.games.GameRepositoryImpl
import com.lowbudgetlcs.repositories.games.ShortcodeCriteria
import com.lowbudgetlcs.repositories.players.PlayerRepositoryImpl
import com.lowbudgetlcs.repositories.teams.TeamRepositoryImpl
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchTeam

class StatDaemon : Worker {
    private val queue = "STATS"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.StatDaemon")
    private val gamesR = GameRepositoryImpl()
    private val playersR = PlayerRepositoryImpl()
    private val teamsR = TeamRepositoryImpl()

    private fun main() {
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
                    gamesR.readByCriteria(ShortcodeCriteria(callback.shortCode)).firstOrNull()?.let { game ->
                        // I DO NOT WANT TO WRITE MY OWN SERIALIZER FOR THIS ITS LIKE 500 FIELDS AHAHAHAHAHHA!
                        // lblcs.gameDumpsQueries.dump(game.id, Json.encodeToString<LOLMatch>(match))
                        // Process Team data first to cause errors as early as possible
                        match.teams.forEach { team ->
                            processTeam(
                                team, match.participants.filter { it.team === team.teamId }, game
                            )
                        }
                        match.participants.forEach { processPlayer(it, game) }
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

    override fun start() {
        this.main()
    }

    private fun processTeam(team: MatchTeam, players: List<MatchParticipant>, game: Game) {
        fetchTeamId(players)?.let { teamId ->
            logger.debug("Processing team data for '{}' ('{}')", teamId, game.shortCode)
            teamsR.readById(teamId)?.let { t ->
                try {
                    teamsR.createTeamData(
                        t,
                        game,
                        team.teamId,
                        team.didWin(),
                        players.sumOf { it.goldEarned },
                        team.objectives,
                    )
                } catch (e: Throwable) {
                    logger.error(
                        "Transaction failed for '{}' ('{}')", t.name, game.shortCode
                    )
                    logger.error(e.message)
                }
            }
        }
    }

    private fun processPlayer(player: MatchParticipant, game: Game) {
        logger.debug(
            "Processing game data for '{}' (code '{}').", "${player.riotIdName}#${player.riotIdTagline}", game.shortCode
        )
        try {
            playersR.readByPuuid(player.puuid)?.let { p ->
                playersR.createPlayerData(
                    p, game, PlayerGameData(
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
                )
            }
            logger.debug(
                "Successfully processed player '{}' ('{}')",
                "${player.riotIdName}#${player.riotIdTagline}",
                game.shortCode
            )
        } catch (e: Throwable) {
            logger.error(
                "Transaction failed for '{}' ('{}')", "${player.riotIdName}#${player.riotIdTagline}", game.shortCode
            )
            logger.error(e.message)
        }
    }
}