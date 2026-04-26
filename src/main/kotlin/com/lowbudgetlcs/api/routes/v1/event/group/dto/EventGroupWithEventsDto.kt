package com.lowbudgetlcs.api.routes.v1.event.group.dto

import com.lowbudgetlcs.api.routes.v1.event.dto.EventDto
import kotlinx.serialization.Serializable

@Serializable
data class EventGroupWithEventsDto(
    val id: Int,
    val name: String,
    val events: List<EventDto>,
)
