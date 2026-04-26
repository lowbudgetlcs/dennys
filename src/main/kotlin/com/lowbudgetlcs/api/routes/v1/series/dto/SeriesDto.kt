package com.lowbudgetlcs.api.routes.v1.series.dto

import com.lowbudgetlcs.domain.event.models.types.EventStage
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDto(
    val id: Int,
    val eventId: Int?,
    val teamIds: List<Int>,
    val totalGames: Int,
    val eventStage: EventStage,
    val result: SeriesResultDto? = null
)
