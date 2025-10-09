package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import java.time.Instant

data class NewEvent(
    val name: String,
    val description: String,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
)

fun NewEvent.toEvent(
    id: EventId,
    createdAt: Instant,
    riotTournamentId: RiotTournamentId,
): Event =
    Event(
        id = id,
        name = name,
        description = description,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        eventGroupId = null,
        status = status,
    )
