package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.EventQuery


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
