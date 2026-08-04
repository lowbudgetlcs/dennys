package com.lowbudgetlcs.api.dto.series

import kotlinx.serialization.Serializable

@Serializable
data class CompleteSeriesDto(
    val winnerTeamId: Int? = null,
    val loserTeamId: Int? = null,
)
