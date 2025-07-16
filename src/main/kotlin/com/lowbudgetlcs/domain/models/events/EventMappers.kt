package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.TournamentId
import java.time.Instant

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