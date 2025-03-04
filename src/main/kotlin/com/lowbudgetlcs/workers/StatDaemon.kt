package com.lowbudgetlcs.workers

import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.bridges.RiotBridge
import com.lowbudgetlcs.entities.*
import com.lowbudgetlcs.repositories.games.AllGamesLBLCS
import com.lowbudgetlcs.repositories.games.IGameRepository
import com.lowbudgetlcs.repositories.games.ShortcodeCriteria
import com.lowbudgetlcs.repositories.players.AllPlayersLBLCS
import com.lowbudgetlcs.repositories.players.IPlayerRepository
import com.lowbudgetlcs.repositories.teams.AllTeamsLBLCS
import com.lowbudgetlcs.repositories.teams.ITeamRepository
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.rabbitmq.client.Delivery
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchTeam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This service worker consumes [RiotCallback]s off of [queue] and saves player
 * and team data into storage.
 */
class StatDaemon private constructor(
    override val queue: String,
    private val gamesR: IGameRepository,
    private val playersR: IPlayerRepository,
    private val teamsR: ITeamRepository
) : AbstractWorker(), IMessageQListener {
    private val logger : Logger = LoggerFactory.getLogger(StatDaemon::class.java)
    private val messageq = RabbitMQBridge(queue)
    private val riot = RiotBridge()

    /**
     * Private constructor and companion object prevent direct instantiation.
     *
     * This behavior is deprecated and will be removed in future versions.
     */
    companion object {
        fun createInstance(queue: String): StatDaemon = StatDaemon(
            queue, AllGamesLBLCS(), AllPlayersLBLCS(), AllTeamsLBLCS()
        )
    }

    override fun createInstance(instanceId: Int): StatDaemon = Companion.createInstance(queue)

    override fun start() {
        logger.info("🚀 StatDaemon is running...")
        logger.debug("📡 Listening on queue: `$queue`")
        messageq.listen { _, delivery ->
            processMessage(delivery)
        }
    }

    /**
     * Consumes [delivery] from [queue] and parses it as a [RiotCallback].
     * Begins stat processing.
     */
    override fun processMessage(delivery: Delivery) {
        val message = String(delivery.body, charset("UTF-8"))
        logger.info("📩 Received message from queue: $message")
        try {
            val callback = Json.decodeFromString<RiotCallback>(message)
            logger.info("✅ Successfully decoded RiotCallback for game ID: ${callback.gameId}")
            processRiotCallback(callback)
            messageq.channel.basicAck(delivery.envelope.deliveryTag, false)
        } catch (e: SerializationException) {
            logger.error("❌ Failed to decode message: $message", e)
        } catch (e: IllegalArgumentException) {
            logger.warn("🚫 Invalid Riot Callback message: $message.", e)
        }
    }

    /**
     * Fetches a match from the RiotAPI derived from [callback]. Then, iterates over
     * each team and saves its game data. Then, iterates over each participant and saves its
     * game data.
     */
    private fun processRiotCallback(callback: RiotCallback) {
        logger.info("🔍 Fetching match details for game ID: ${callback.gameId}")
        riot.match(callback.gameId)?.let { match ->
            gamesR.readByCriteria(ShortcodeCriteria(callback.shortCode)).firstOrNull()?.let { game ->
                logger.info("🎮 Processing match `${match.gameId}` for shortcode `${game.shortCode}`...")
                // I DO NOT WANT TO WRITE MY OWN SERIALIZER FOR THIS ITS LIKE 500 FIELDS AHAHAHAHAHHA!
                // lblcs.gameDumpsQueries.dump(game.id, Json.encodeToString<LOLMatch>(match))
                // Process Team data first to cause errors as early as possible
                match.teams.forEach { team ->
                    processTeam(
                        team, match.participants.filter { it.team === team.teamId }, game, match.gameDuration.toLong()
                    )
                }

                match.participants.forEach { processPlayer(it, game) }

                logger.info("✅ Finished processing match `${match.gameId}` for shortcode `${game.shortCode}`")
            }
        }
    }

    /**
     * Saves game data for a [team] consisting of [players] from [game]. [length] is the game duration.
     */
    private fun processTeam(team: MatchTeam, players: List<MatchParticipant>, game: Game, length: Long) {
        playersR.fetchTeamId(players)?.let { teamId ->
            logTransactionMessage("📝 Saving game data for", teamId.toString(), game.shortCode, "...")
            teamsR.readById(teamId)?.let { t ->
                try {
                    val side = if (team.teamId === TeamType.BLUE) RiftSide.BLUE else RiftSide.RED
                    teamsR.saveTeamData(
                        t, game, TeamGameData(
                            team.didWin(), side, players.sumOf { it.goldEarned }, length, kills = Objective(
                                kills = team.objectives["champion"]?.kills ?: 0,
                                first = team.objectives["champion"]?.isFirst ?: false
                            ), barons = Objective(
                                kills = team.objectives["baron"]?.kills ?: 0,
                                first = team.objectives["baron"]?.isFirst ?: false
                            ), grubs = Objective(
                                kills = team.objectives["horde"]?.kills ?: 0,
                                first = team.objectives["horde"]?.isFirst ?: false
                            ), dragons = Objective(
                                kills = team.objectives["dragon"]?.kills ?: 0,
                                first = team.objectives["dragon"]?.isFirst ?: false
                            ), heralds = Objective(
                                kills = team.objectives["riftHerald"]?.kills ?: 0,
                                first = team.objectives["riftHerald"]?.isFirst ?: false
                            ), towers = Objective(
                                kills = team.objectives["tower"]?.kills ?: 0,
                                first = team.objectives["tower"]?.isFirst ?: false
                            ), inhibitors = Objective(
                                kills = team.objectives["inhibitor"]?.kills ?: 0,
                                first = team.objectives["inhibitor"]?.isFirst ?: false
                            )
                        )
                    )
                    logTransactionMessage("✅ Saved game data for", t.name, game.shortCode)
                } catch (e: Throwable) {
                    transactionError(e, t.name, game.shortCode)
                }
            }
        }
    }

    /**
     * Saves game data for [player] derived from [game].
     */
    private fun processPlayer(player: MatchParticipant, game: Game) {
        logTransactionMessage(
            "📝 Saving game data for", "${player.riotIdName}#${player.riotIdTagline}", game.shortCode, "..."
        )
        try {
            playersR.readByPuuid(player.puuid)?.let { p ->
                playersR.savePlayerData(
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
            logTransactionMessage("✅ Saved stats for", "${player.riotIdName}#${player.riotIdTagline}", game.shortCode)
        } catch (e: Throwable) {
            transactionError(e, "${player.riotIdName}#${player.riotIdTagline}", game.shortCode)
        }
    }

    /**
     * Logs debug info during processing.
     */
    private fun logTransactionMessage(preamble: String, target: String, context: String, closer: String = ".") {
        logger.debug("$preamble '$target' ('$context')$closer")
    }

    /**
     * Logs errors during processing.
     */
    private fun transactionError(e: Throwable, target: String, context: String) {
        logger.error("❌ Failed to save stats for '$target' ('$context')", e)
    }
}