package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.models.events.Stage
import kotlinx.serialization.Serializable

@Serializable
data class NewSeriesDto(
    val team1Id: Int,
    val team2Id: Int,
    val totalGames: Int,
    val stage: Stage,
)
