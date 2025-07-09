package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.event.EventStatus
import java.time.OffsetDateTime

data class EventDto(
    val id: Int,
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    val createdAt: OffsetDateTime,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: EventStatus
)