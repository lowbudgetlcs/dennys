package com.lowbudgetlcs.domain.event.routes.dto

import com.lowbudgetlcs.domain.event.models.EventQuery


data class EventFilterParams(
    val name: String?,
    val status: String?,
)

// Extensions
fun EventFilterParams.toQuery(): EventQuery =
    EventQuery(
        name = name,
        status = status?.toEventStatus(),
    )
