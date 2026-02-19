package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.EventStatus

data class EventQuery(
    val name: String?,
    val status: EventStatus?,
)
