package com.lowbudgetlcs.domain.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.PlayerGameStats
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.TeamGameStats
import kotlinx.serialization.json.JsonObject

/**
 * Per-game lookups throw [NoSuchElementException] for an unknown game or one without that data stored yet.
 * Row lookups throw [IllegalArgumentException] unless `limit` is 1..1000 and `offset` is not negative.
 */
interface IStatsService {
    fun getMatch(gameId: GameId): JsonObject

    fun getTimeline(gameId: GameId): JsonObject

    fun getGameStats(gameId: GameId): GameStats

    fun getPlayerGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<PlayerGameStats>

    fun getTeamGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamGameStats>
}
