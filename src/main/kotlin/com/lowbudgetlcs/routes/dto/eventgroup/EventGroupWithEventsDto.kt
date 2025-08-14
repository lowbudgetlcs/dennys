package com.lowbudgetlcs.routes.dto.eventgroup

import com.lowbudgetlcs.routes.dto.events.EventDto
import kotlinx.serialization.Serializable

@Serializable
data class EventGroupWithEventsDto(
    val id: Integer, val name: String, val events: List<EventDto>
)