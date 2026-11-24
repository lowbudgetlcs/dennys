package com.lowbudgetlcs.domain.stats.models

data class GameStats(
    val players: List<PlayerGameStats>,
    val teams: List<TeamGameStats>,
)
