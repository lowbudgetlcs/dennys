package com.lowbudgetlcs.api.dto.eventgroup

import kotlinx.serialization.Serializable

@Serializable
data class EventGroupAddEventDto(
    val eventId: Int,
)
