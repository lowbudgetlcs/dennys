package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.domain.event.models.types.EventStatus

data class EventFilterParams(
    val name: String?,
    val status: EventStatus?,
)
