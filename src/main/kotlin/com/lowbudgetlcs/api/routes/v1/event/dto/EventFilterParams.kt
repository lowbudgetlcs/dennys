package com.lowbudgetlcs.api.routes.v1.event.dto

import com.lowbudgetlcs.domain.event.models.types.EventStatus

data class EventFilterParams(
    val name: String?,
    val status: EventStatus?,
)
