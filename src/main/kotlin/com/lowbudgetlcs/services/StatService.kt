package com.lowbudgetlcs.services

import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.models.match.MatchParticipant
import com.lowbudgetlcs.models.match.MatchTeam
import com.lowbudgetlcs.models.match.TeamType
import com.lowbudgetlcs.repositories.IGameRepository
import com.lowbudgetlcs.repositories.IMatchRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The StatService saves game-data (such as KDA) for LeagueOfLegendsMatch-es
 */
class StatService(
    private val gamesRepository: IGameRepository,
    private val playersRepository: IPlayerRepository,
    private val teamsRepository: ITeamRepository,
    private val matchRepository: IMatchRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(StatService::class.java)

    /**
     * Entrypoint for callback stat-processing.
     */
    suspend fun process(callback: PostMatchCallback) {
        logger.info("🔍 Fetching match details for game ID: ${callback.gameId}")
        matchRepository.getMatch(callback.gameId)?.let { match ->
            gamesRepository.get(shortcode = callback.shortCode)?.let { game ->
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
        playersRepository.fetchTeamId(players)?.let { teamId ->
            logger.debug("\uD83D\uDCDD Saving game data for {} ({}...", teamId, game.shortCode)
            teamsRepository.get(id = teamId)?.let { t ->
                try {
                    val side = if (team.teamId == TeamType.BLUE.code) RiftSide.BLUE else RiftSide.RED
                    if (teamsRepository.saveTeamData(
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
                        ) != null
                    ) logger.debug("✅ Saved game data for ${t.name} (${game.shortCode}).")
                    else logger.debug("❌ Failed to save game data for ${t.name} (${game.shortCode}.)")
                } catch (e: Throwable) {
                    logger.error("Exception: ", e)
                }
            }
        }


    /**
     * Saves game data for [player] derived from [game].
     */
    private fun processPlayer(player: MatchParticipant, game: Game) {
        logger.debug(
            "📝 Saving game data for ${player.riotGameName}#${player.riotTagline} (game.shortCode)..."
        )
        try {
            playersRepository.get(puuid = player.playerUniqueUserId)?.let { p ->
                if (playersRepository.savePlayerData(
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
                    ) != null
                ) logger.debug("✅ Saved stats for ${player.riotGameName}#${player.riotTagline} (${game.shortCode}).")
                else logger.debug("❌ Failed to save stats data for ${player.riotGameName}#${player.riotTagline} (game.shortCode).")
            }
        } catch (e: Throwable) {
            logger.error("Exception occured:", e)
        }
    }
}