package com.lowbudgetlcs.domain.stats.models

data class ChampionAggregate(
    val championId: Int,
    val championName: String?,
    val picks: Int,
    val wins: Int,
    val bans: Int,
    val totals: StatTotals,
)
