package com.lowbudgetlcs.domain.models.event

import java.time.OffsetDateTime

data class NewEvent(
    val name: String,
    val description: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: EventStatus
)
