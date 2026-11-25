package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.stats.models.ChampionAggregate
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.stats.models.TeamAggregate
import com.lowbudgetlcs.domain.stats.models.TeamRecord

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

    /**
     * Series and game W-L from recorded results, so forfeits and games without match data count. A double loss is a
     * loss for both teams. Only the event group, event, stage and team filters apply.
     */
    fun getTeamRecords(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamRecord>

    /** Bans are counted on the team rows the query matches, so `teamId` and `playerId` count that side's bans. */
    fun getChampionAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
        minGames: Int? = null,
    ): List<ChampionAggregate>
}
