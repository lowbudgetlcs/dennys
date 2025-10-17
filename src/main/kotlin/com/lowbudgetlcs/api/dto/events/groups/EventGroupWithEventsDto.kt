package com.lowbudgetlcs.api.dto.events.groups

import com.lowbudgetlcs.api.dto.events.EventDto
import kotlinx.serialization.Serializable

@Serializable
data class EventGroupWithEventsDto(
    val id: Int,
    val name: String,
    val events: List<EventDto>,
)
