package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.GameStats

interface IStatsRepository {
    /** Every player and team row for a game; both lists are empty when it has no Riot match data. */
    fun getGameStats(gameId: GameId): GameStats
}
