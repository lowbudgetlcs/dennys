package com.lowbudgetlcs.api.dto.stats

import kotlinx.serialization.Serializable

@Serializable
data class TeamRecordDto(
    val teamId: Int,
    val teamName: String?,
    val seriesWon: Int,
    val seriesLost: Int,
    val gamesWon: Int,
    val gamesLost: Int,
)
