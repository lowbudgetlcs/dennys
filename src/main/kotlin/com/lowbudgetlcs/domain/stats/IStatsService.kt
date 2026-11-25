package com.lowbudgetlcs.domain.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.ChampionAggregate
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.PlayerGameStats
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.stats.models.TeamAggregate
import com.lowbudgetlcs.domain.stats.models.TeamGameStats
import com.lowbudgetlcs.domain.stats.models.TeamRecord
import kotlinx.serialization.json.JsonObject

/**
 * Per-game lookups throw [NoSuchElementException] for an unknown game or one without that data stored yet.
 * Row and aggregate lookups throw [IllegalArgumentException] unless `limit` is 1..1000 and `offset` is not negative,
 * and aggregates throw [com.lowbudgetlcs.domain.InvalidRequestException] for a sort that isn't a sortable stat.
 * A null sort means most games (or picks) first.
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

    fun getPlayerAggregates(
        query: StatsQuery,
        sort: StatsSort?,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<PlayerAggregate>

    fun getTeamAggregates(
        query: StatsQuery,
        sort: StatsSort?,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<TeamAggregate>

    fun getTeamRecords(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamRecord>

    fun getChampionAggregates(
        query: StatsQuery,
        sort: StatsSort?,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<ChampionAggregate>
}
