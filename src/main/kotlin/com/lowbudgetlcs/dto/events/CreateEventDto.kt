package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.event.EventStatus
import java.time.Instant

data class CreateEventDto(
    val name: String,
    val description: String,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)
