package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.EventStatus

data class EventQuery(
    val name: EventName?,
    val status: EventStatus?,
)
