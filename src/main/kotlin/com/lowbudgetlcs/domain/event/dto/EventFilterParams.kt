package com.lowbudgetlcs.domain.event.dto

import com.lowbudgetlcs.domain.event.models.EventStatus

data class EventFilterParams(
    val name: String?,
    val status: EventStatus?,
)
