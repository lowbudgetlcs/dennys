package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus

data class EventQuery(
    val name: String?,
    val status: EventStatus?,
)
