package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.event.core.model.types.toEventName
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
    val status: String,
    val eventStages: Set<String>,
)

// Extensions
fun CreateEventDto.toNewEvent(): NewEvent =
    NewEvent(
        name = name.toEventName(),
        description = description.toEventDescription(),
        startDate = startDate,
        endDate = endDate,
        status = status.toEventStatus(),
        eventStages = eventStages.map { it.toEventStage() }.toSet(),
    )
