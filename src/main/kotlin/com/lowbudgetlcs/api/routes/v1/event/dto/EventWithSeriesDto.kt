package com.lowbudgetlcs.api.routes.v1.event.dto

import com.lowbudgetlcs.api.routes.v1.series.dto.SeriesDto
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventWithSeriesDto(
    val id: Int,
    val name: String,
    val description: String,
    @Contextual val createdAt: Instant,
    @Contextual val startDate: Instant,
    @Contextual val endDate: Instant,
    val status: EventStatus,
    val eventGroupId: Int? = null,
    val series: List<SeriesDto>,
    val eventStages: Set<EventStage>,
)
