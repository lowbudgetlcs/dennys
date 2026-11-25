package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId

data class PlayerAggregate(
    val playerId: PlayerId,
    val playerName: String?,
    val games: Int,
    val wins: Int,
    val seriesPlayed: Int,
    val seriesWon: Int,
    val seriesLost: Int,
    val totals: StatTotals,
)
