package com.lowbudgetlcs.domain.models.events

import java.time.Instant

data class NewEvent(
    val name: String, val description: String, val startDate: Instant, val endDate: Instant, val status: EventStatus
)
