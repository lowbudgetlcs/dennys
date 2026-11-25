package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.stats.models.StatsQuery
import org.jooq.Condition
import org.jooq.impl.DSL.exists
import org.jooq.impl.DSL.selectOne
import org.jooq.storage.tables.references.PLAYER_GAME_STATS_V1
import org.jooq.storage.tables.references.TEAM_GAME_STATS_V1

internal fun playerConditions(query: StatsQuery): List<Condition> =
    listOfNotNull(
        query.eventGroupId?.let { PLAYER_GAME_STATS_V1.EVENT_GROUP_ID.eq(it.value) },
        query.eventId?.let { PLAYER_GAME_STATS_V1.EVENT_ID.eq(it.value) },
        query.stage?.let { PLAYER_GAME_STATS_V1.STAGE.eq(it.name) },
        query.teamId?.let { PLAYER_GAME_STATS_V1.TEAM_ID.eq(it.value) },
        query.playerId?.let { PLAYER_GAME_STATS_V1.PLAYER_ID.eq(it.value) },
        query.championId?.let { PLAYER_GAME_STATS_V1.CHAMPION_ID.eq(it) },
        query.minDurationSeconds?.let { PLAYER_GAME_STATS_V1.GAME_DURATION_S.ge(it) },
    )

/** [StatsQuery.playerId] keeps the side that player played on; [StatsQuery.championId] doesn't apply to team rows. */
internal fun teamConditions(query: StatsQuery): List<Condition> =
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
        query.minDurationSeconds?.let { TEAM_GAME_STATS_V1.GAME_DURATION_S.ge(it) },
    )
