package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import java.time.Instant

data class NewEvent(
    val name: EventName,
    val description: EventDescription,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val eventGroupId: EventGroupId? = null,
    val eventStages: Set<EventStage>,
)

// Extensions
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
        eventStages = eventStages,
    )
