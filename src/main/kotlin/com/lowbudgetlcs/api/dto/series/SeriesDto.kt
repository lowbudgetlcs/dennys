package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.event.models.EventStage
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDto(
    val id: Int,
    val eventId: Int?,
    val teamIds: List<Int>,
    val totalGames: Int,
    val eventStage: EventStage,
)
