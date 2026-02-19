package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus

data class EventFilterParams(
    val name: String?,
    val status: EventStatus?,
)
