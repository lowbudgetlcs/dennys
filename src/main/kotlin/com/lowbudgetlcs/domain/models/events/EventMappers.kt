package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import java.time.Instant

fun NewEventGroup.toEventGroup(id: EventGroupId): EventGroup = EventGroup(
    id = id,
    name = name
)

fun NewEvent.toEvent(id: EventId, createdAt: Instant, riotTournamentId: RiotTournamentId): Event = Event(
    id = id,
    name = name,
    description = description,
    riotTournamentId = riotTournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    eventGroupId = null,
    status = status
)