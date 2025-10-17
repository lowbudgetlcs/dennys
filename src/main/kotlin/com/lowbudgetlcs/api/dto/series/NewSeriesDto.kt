package com.lowbudgetlcs.api.dto.series

import kotlinx.serialization.Serializable

@Serializable
data class NewSeriesDto(
    val team1Id: Int,
    val team2Id: Int,
    val gamesToWin: Int,
)
