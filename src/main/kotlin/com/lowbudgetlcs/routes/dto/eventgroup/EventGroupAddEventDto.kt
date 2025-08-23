package com.lowbudgetlcs.routes.dto.eventgroup

import kotlinx.serialization.Serializable

@Serializable
data class EventGroupAddEventDto(
    val eventId: Int
)