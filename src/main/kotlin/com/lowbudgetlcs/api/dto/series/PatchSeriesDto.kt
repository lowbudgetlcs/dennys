package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import kotlinx.serialization.Serializable

@Serializable
data class PatchSeriesDto(
    val status: SeriesStatus,
    val winnerTeamId: Int? = null,
    val doubleLoss: Boolean = false,
)
