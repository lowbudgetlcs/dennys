package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStatus

data class EventQuery(
    val name: String?,
    val status: EventStatus?,
)
