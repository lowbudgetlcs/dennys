package com.lowbudgetlcs.domain.event.dto

import com.lowbudgetlcs.domain.event.models.EventStage
import com.lowbudgetlcs.domain.event.models.EventStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CreateEventDto(
    val name: String,
    val description: String,
    @Contextual
    val startDate: Instant,
    @Contextual
    val endDate: Instant,
    val status: EventStatus,
    val eventStages: Set<EventStage>,
)
