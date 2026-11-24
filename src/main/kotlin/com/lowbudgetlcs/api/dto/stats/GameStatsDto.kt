package com.lowbudgetlcs.api.dto.stats

import kotlinx.serialization.Serializable

@Serializable
data class GameStatsDto(
    val players: List<PlayerGameStatsDto>,
    val teams: List<TeamGameStatsDto>,
)
