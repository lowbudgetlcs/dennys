package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.model.EventQuery


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
