package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.event.EventStatus
import java.time.Instant

data class EventDto(
    val id: Int,
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)