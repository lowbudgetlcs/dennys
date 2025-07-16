package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.TournamentId
import java.time.Instant

data class EventWithGroup(
    val id: EventId,
    val name: String,
    val description: String,
    val eventGroup: EventGroup?,
    val tournamentId: TournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)
