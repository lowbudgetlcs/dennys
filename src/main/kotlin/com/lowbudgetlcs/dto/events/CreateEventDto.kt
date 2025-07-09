package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.event.EventStatus
import java.time.OffsetDateTime

data class CreateEventDto(
    val name: String,
    val description: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: EventStatus
)
