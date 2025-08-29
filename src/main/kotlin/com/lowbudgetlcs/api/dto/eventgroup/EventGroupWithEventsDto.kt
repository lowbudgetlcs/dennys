package com.lowbudgetlcs.api.dto.eventgroup

import com.lowbudgetlcs.api.dto.events.EventDto
import kotlinx.serialization.Serializable

@Serializable
data class EventGroupWithEventsDto(
    val id: Integer, val name: String, val events: List<EventDto>
)