package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupId
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.PlayerGameStats
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.TeamGameStats
import com.lowbudgetlcs.domain.team.models.toTeamId
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.exists
import org.jooq.impl.DSL.selectOne
import org.jooq.storage.tables.records.PlayerGameStatsV1Record
import org.jooq.storage.tables.records.TeamGameStatsV1Record
import org.jooq.storage.tables.references.PLAYER_GAME_STATS_V1
import org.jooq.storage.tables.references.TEAM_GAME_STATS_V1

class StatsRepository(
    private val dsl: DSLContext,
) : IStatsRepository {
    override fun getGameStats(gameId: GameId): GameStats =
        GameStats(
            players =
                selectPlayers(listOf(PLAYER_GAME_STATS_V1.GAME_ID.eq(gameId.value)))
                    .fetch()
                    .mapNotNull(::recordToPlayerGameStats),
            teams =
                selectTeams(listOf(TEAM_GAME_STATS_V1.GAME_ID.eq(gameId.value)))
                    .fetch()
                    .mapNotNull(::recordToTeamGameStats),
        )

    override fun getPlayerGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<PlayerGameStats> {
        val conditions =
            listOfNotNull(
                query.eventGroupId?.let { PLAYER_GAME_STATS_V1.EVENT_GROUP_ID.eq(it.value) },
                query.eventId?.let { PLAYER_GAME_STATS_V1.EVENT_ID.eq(it.value) },
                query.stage?.let { PLAYER_GAME_STATS_V1.STAGE.eq(it.name) },
                query.teamId?.let { PLAYER_GAME_STATS_V1.TEAM_ID.eq(it.value) },
                query.playerId?.let { PLAYER_GAME_STATS_V1.PLAYER_ID.eq(it.value) },
                query.championId?.let { PLAYER_GAME_STATS_V1.CHAMPION_ID.eq(it) },
            )
        return selectPlayers(conditions)
            .limit(limit)
            .offset(offset)
            .fetch()
            .mapNotNull(::recordToPlayerGameStats)
    }

    override fun getTeamGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamGameStats> {
        val conditions =
            listOfNotNull(
                query.eventGroupId?.let { TEAM_GAME_STATS_V1.EVENT_GROUP_ID.eq(it.value) },
                query.eventId?.let { TEAM_GAME_STATS_V1.EVENT_ID.eq(it.value) },
                query.stage?.let { TEAM_GAME_STATS_V1.STAGE.eq(it.name) },
                query.teamId?.let { TEAM_GAME_STATS_V1.TEAM_ID.eq(it.value) },
                query.playerId?.let { playerId ->
                    exists(
                        selectOne()
                            .from(PLAYER_GAME_STATS_V1)
                            .where(PLAYER_GAME_STATS_V1.GAME_ID.eq(TEAM_GAME_STATS_V1.GAME_ID))
                            .and(PLAYER_GAME_STATS_V1.TEAM_ID.eq(TEAM_GAME_STATS_V1.TEAM_ID))
                            .and(PLAYER_GAME_STATS_V1.PLAYER_ID.eq(playerId.value)),
                    )
                },
            )
        return selectTeams(conditions)
            .limit(limit)
            .offset(offset)
            .fetch()
            .mapNotNull(::recordToTeamGameStats)
    }

    private fun selectPlayers(conditions: List<Condition>) =
        dsl
            .selectFrom(PLAYER_GAME_STATS_V1)
            .where(conditions)
            .orderBy(
                PLAYER_GAME_STATS_V1.GAME_ID.desc(),
                PLAYER_GAME_STATS_V1.TEAM_ID,
                PLAYER_GAME_STATS_V1.POSITION.sortAsc("TOP", "JUNGLE", "MIDDLE", "BOTTOM", "UTILITY"),
                PLAYER_GAME_STATS_V1.PUUID,
            )

    private fun selectTeams(conditions: List<Condition>) =
        dsl
            .selectFrom(TEAM_GAME_STATS_V1)
            .where(conditions)
            .orderBy(TEAM_GAME_STATS_V1.GAME_ID.desc(), TEAM_GAME_STATS_V1.SIDE)

    private fun recordToPlayerGameStats(record: PlayerGameStatsV1Record): PlayerGameStats? {
        val gameId = record.gameId?.toGameId() ?: return null
        val seriesId = record.seriesId?.toSeriesId() ?: return null
        val eventId = record.eventId?.toEventId() ?: return null
        val stage = record.stage?.let { EventStage.valueOf(it) } ?: return null
        return PlayerGameStats(
            gameId = gameId,
            seriesId = seriesId,
            eventId = eventId,
            eventGroupId = record.eventGroupId?.toEventGroupId(),
            stage = stage,
            teamId = record.teamId?.toTeamId(),
            playerId = record.playerId?.toPlayerId(),
            gameDurationS = record.gameDurationS ?: 0,
            puuid = record.puuid,
            riotIdGameName = record.riotIdGameName,
            riotIdTagline = record.riotIdTagline,
            championId = record.championId ?: 0,
            championName = record.championName,
            position = record.position,
            win = record.win ?: false,
            kills = record.kills ?: 0,
            deaths = record.deaths ?: 0,
            assists = record.assists ?: 0,
            cs = record.cs ?: 0,
            goldEarned = record.goldEarned ?: 0,
            champLevel = record.champLevel ?: 0,
            damageToChampions = record.damageToChampions ?: 0,
            damageTaken = record.damageTaken ?: 0,
            visionScore = record.visionScore ?: 0,
            wardsPlaced = record.wardsPlaced ?: 0,
            wardsKilled = record.wardsKilled ?: 0,
            controlWardsBought = record.controlWardsBought ?: 0,
            voidGrubs = record.voidGrubs ?: 0,
        )
    }

    private fun recordToTeamGameStats(record: TeamGameStatsV1Record): TeamGameStats? {
        val gameId = record.gameId?.toGameId() ?: return null
        val seriesId = record.seriesId?.toSeriesId() ?: return null
        val eventId = record.eventId?.toEventId() ?: return null
        val stage = record.stage?.let { EventStage.valueOf(it) } ?: return null
        return TeamGameStats(
            gameId = gameId,
            seriesId = seriesId,
            eventId = eventId,
            eventGroupId = record.eventGroupId?.toEventGroupId(),
            stage = stage,
            teamId = record.teamId?.toTeamId(),
            side = record.side,
            gameDurationS = record.gameDurationS ?: 0,
            win = record.win ?: false,
            firstBlood = record.firstBlood ?: false,
            firstTower = record.firstTower ?: false,
            firstDragon = record.firstDragon ?: false,
            firstBaron = record.firstBaron ?: false,
            kills = record.kills ?: 0,
            towerKills = record.towerKills ?: 0,
            inhibitorKills = record.inhibitorKills ?: 0,
            dragonKills = record.dragonKills ?: 0,
            baronKills = record.baronKills ?: 0,
            riftHeraldKills = record.riftHeraldKills ?: 0,
            hordeKills = record.hordeKills ?: 0,
            bans = record.bans?.filterNotNull() ?: emptyList(),
        )
    }
}
