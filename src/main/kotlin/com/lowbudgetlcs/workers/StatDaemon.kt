package com.lowbudgetlcs.workers

import com.lowbudgetlcs.bridges.RabbitMQBridge
import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.models.match.MatchParticipant
import com.lowbudgetlcs.models.match.MatchTeam
import com.lowbudgetlcs.models.match.TeamType
import com.lowbudgetlcs.repositories.games.AllGamesDatabase
import com.lowbudgetlcs.repositories.games.IGameRepository
import com.lowbudgetlcs.repositories.games.ShortcodeCriteria
import com.lowbudgetlcs.repositories.players.AllPlayersDatabase
import com.lowbudgetlcs.repositories.players.IPlayerRepository
import com.lowbudgetlcs.repositories.riot.MatchRepositoryRiot
import com.lowbudgetlcs.repositories.teams.AllTeamsDatabase
import com.lowbudgetlcs.repositories.teams.ITeamRepository
import com.lowbudgetlcs.routes.riot.RiotCallback
import com.lowbudgetlcs.util.RateLimiter
import com.rabbitmq.client.Delivery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
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
    private val teamsR: ITeamRepository,
    private val riotMatchRepository: MatchRepositoryRiot
) : AbstractWorker(), IMessageQListener {

    private val logger: Logger = LoggerFactory.getLogger(StatDaemon::class.java)
    private val messageq = RabbitMQBridge(queue)

    /**
     * Private constructor and companion object prevent direct instantiation.
     *
     * This behavior is deprecated and will be removed in future versions.
     */
    companion object {
        fun createInstance(queue: String): StatDaemon = StatDaemon(
            queue,
            AllGamesDatabase(),
            AllPlayersDatabase(),
            AllTeamsDatabase(),
            MatchRepositoryRiot(RiotApiClient(), RateLimiter())
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
        logger.info("📩 StatDaemon recieved a message!")
        try {
            val callback = Json.decodeFromString<RiotCallback>(message)
            logger.debug("✅ Successfully decoded RiotCallback for game ID: ${callback.gameId}")
            CoroutineScope(Dispatchers.IO).launch {
                processRiotCallback(callback)
            }
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
    private suspend fun processRiotCallback(callback: RiotCallback) {
        logger.info("🔍 Fetching match details for game ID: ${callback.gameId}")

        val tournamentMatch = riotMatchRepository.getMatch(callback.gameId)
        tournamentMatch?.let { match ->
            gamesR.readByCriteria(ShortcodeCriteria(callback.shortCode)).firstOrNull()?.let { game ->
                logger.info("🎮 Processing match `${match.matchInfo.gameId}` for shortcode `${game.shortCode}`...")

                match.matchInfo.teams.forEach { team ->
                    val allPlayersOnSameTeam = match.matchInfo.participants.filter { it.teamId == team.teamId }
                    val gameDurationAsLong = match.matchInfo.gameDuration.toLong()

                    processTeam(team = team, players = allPlayersOnSameTeam, game = game, length = gameDurationAsLong)
                }

                match.matchInfo.participants.forEach { player ->
                    processPlayer(player, game)
                }

                logger.info("✅ Finished processing match `${match.matchInfo.gameId}` for shortcode `${game.shortCode}`")
            }
        }
    }

    /**
     * Saves game data for a [team] consisting of [players] from [game]. [length] is the game duration.
     */
    private fun processTeam(team: MatchTeam, players: List<MatchParticipant>, game: Game, length: Long) =
        playersR.fetchTeamId(players)?.let { teamId ->
            logDebugMessage("📝 Saving game data for", teamId.toString(), game.shortCode, "...")
            teamsR.readById(teamId)?.let { t ->
                try {
                    val side = if (team.teamId == TeamType.BLUE.code) RiftSide.BLUE else RiftSide.RED
                    if (teamsR.saveTeamData(
                        t, game, TeamGameData(
                            team.win, side, players.sumOf { it.goldEarned }, length, kills = Objective(
                                kills = team.objectives.champion?.kills ?: 0,
                                first = team.objectives.champion?.firstTaken == true
                            ), barons = Objective(
                                kills = team.objectives.baron?.kills ?: 0,
                                first = team.objectives.baron?.firstTaken == true
                            ), grubs = Objective(
                                kills = team.objectives.horde?.kills ?: 0,
                                first = team.objectives.horde?.firstTaken == true
                            ), dragons = Objective(
                                kills = team.objectives.dragon?.kills ?: 0,
                                first = team.objectives.dragon?.firstTaken == true
                            ), heralds = Objective(
                                kills = team.objectives.riftHerald?.kills ?: 0,
                                first = team.objectives.riftHerald?.firstTaken == true
                            ), towers = Objective(
                                kills = team.objectives.tower?.kills ?: 0,
                                first = team.objectives.tower?.firstTaken == true
                            ), inhibitors = Objective(
                                kills = team.objectives.inhibitor?.kills ?: 0,
                                first = team.objectives.inhibitor?.firstTaken == true
                            )
                        )
                    ) != null) logDebugMessage("✅ Saved game data for", t.name, game.shortCode)
                    else logDebugMessage("❌ Failed to save game data for", t.name, game.shortCode)
                } catch (e: Throwable) {
                    logError(e, t.name, game.shortCode)
                }
            }
        }


    /**
     * Saves game data for [player] derived from [game].
     */
    private fun processPlayer(player: MatchParticipant, game: Game) {
        logDebugMessage(
            "📝 Saving game data for", "${player.riotGameName}#${player.riotTagline}", game.shortCode, "..."
        )
        try {
            playersR.readByPuuid(player.playerUniqueUserId)?.let { p ->
                if (playersR.savePlayerData(
                    p, game, PlayerGameData(
                        player.kills,
                        player.deaths,
                        player.assists,
                        player.championLevel,
                        player.goldEarned.toLong(),
                        player.visionScore,
                        player.totalDamageToChampions,
                        player.totalHealsOnTeammates,
                        player.totalDamageShieldedOnTeammates,
                        player.totalDamageTaken,
                        player.damageSelfMitigated,
                        player.damageDealtToTurrets,
                        player.longestTimeSpentLiving,
                        player.doubleKills,
                        player.tripleKills,
                        player.quadraKills,
                        player.pentaKills,
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
                ) != null) logDebugMessage("✅ Saved stats for", "${player.riotGameName}#${player.riotTagline}", game.shortCode)
                else logDebugMessage("❌ Failed to save stats data for", "${player.riotGameName}#${player.riotTagline}", game.shortCode)
            }
        } catch (e: Throwable) {
            logError(e, "${player.riotGameName}#${player.riotTagline}", game.shortCode)
        }
    }

    /**
     * Logs debug info during processing.
     */
    private fun logDebugMessage(preamble: String, target: String, context: String, closer: String = ".") {
        logger.debug("$preamble '$target' ('$context')$closer")
    }

    /**
     * Logs errors during processing.
     */
    private fun logError(e: Throwable, target: String, context: String) {
        logger.error("❌ Error occured for '$target' ('$context')", e)
    }
}