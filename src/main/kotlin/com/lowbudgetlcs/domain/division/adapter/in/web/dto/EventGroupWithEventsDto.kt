package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.EventDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.core.event.model.EventGroupWithEvents
import kotlinx.serialization.Serializable

@Serializable
data class EventGroupWithEventsDto(
    val id: Int,
    val name: String,
    val events: List<EventDto>,
)

// Extensions
fun EventGroupWithEvents.toDto(): EventGroupWithEventsDto =
    EventGroupWithEventsDto(
        id = id.value,
        name = name.value,
        events = events.map { it.toDto() },
    )
