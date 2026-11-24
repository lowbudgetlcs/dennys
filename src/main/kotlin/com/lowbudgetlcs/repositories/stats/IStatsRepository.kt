package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.PlayerGameStats
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.TeamGameStats

interface IStatsRepository {
    /** Every player and team row for a game; both lists are empty when it has no Riot match data. */
    fun getGameStats(gameId: GameId): GameStats

    fun getPlayerGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<PlayerGameStats>

    /** [StatsQuery.playerId] keeps the rows of the side that player played on; [StatsQuery.championId] is ignored. */
    fun getTeamGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamGameStats>
}
