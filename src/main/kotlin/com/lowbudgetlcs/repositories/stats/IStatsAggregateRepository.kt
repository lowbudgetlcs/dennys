package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.stats.models.ChampionAggregate
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.stats.models.TeamAggregate

/**
 * Each lookup throws [com.lowbudgetlcs.domain.InvalidRequestException] when [StatsSort.stat] isn't a sortable stat.
 * `minGames` (picks, for champions) drops smaller groups before sorting and paging.
 */
interface IStatsAggregateRepository {
    /** PUUIDs not linked to a player are left out. */
    fun getPlayerAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<PlayerAggregate>

    fun getTeamAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<TeamAggregate>

    /** Bans are counted on the team rows the query matches, so `teamId` and `playerId` count that side's bans. */
    fun getChampionAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<ChampionAggregate>
}
