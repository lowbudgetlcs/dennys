package com.lowbudgetlcs.domain.event.core.model

import com.lowbudgetlcs.domain.event.core.model.enums.EventStatus

data class EventQuery(
    val name: String?,
    val status: EventStatus?,
)
