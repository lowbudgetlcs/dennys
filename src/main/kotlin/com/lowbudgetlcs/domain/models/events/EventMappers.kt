package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.tournament.TournamentId
import java.time.Instant

fun NewEventGroup.toEventGroup(id: EventGroupId): EventGroup = EventGroup(
    id = id,
    name = name
)

fun Event.toEventWithGroup(group: EventGroup?): EventWithGroup = EventWithGroup(
    id = id,
    name = name,
    description = description,
    eventGroup = group,
    tournamentId = tournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    status = status
)

fun NewEvent.toEvent(id: EventId, createdAt: Instant, tournamentId: TournamentId): Event = Event(
    id = id,
    name = name,
    description = description,
    tournamentId = tournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    eventGroupId = eventGroupId,
    status = status
)