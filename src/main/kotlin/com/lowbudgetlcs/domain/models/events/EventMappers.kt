package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.tournament.TournamentId
import java.time.Instant

fun NewEventGroup.toEventGroup(id: EventGroupId): EventGroup = EventGroup(
    id = id, name = name
)

fun NewEvent.toEvent(id: EventId, createdAt: Instant, tournamentId: TournamentId): Event = Event(
    id = id,
    name = name,
    description = description,
    tournamentId = tournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    eventGroupId = null,
    status = status
)

fun Event.toEventWithTeams(teams: List<Team>): EventWithTeams = EventWithTeams(
    id = id,
    name = name,
    description = description,
    eventGroupId = eventGroupId,
    tournamentId = tournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    status = status,
    teams = teams
)

fun EventWithTeams.toEvent(): Event = Event(
    id = id,
    name = name,
    description = description,
    eventGroupId = eventGroupId,
    tournamentId = tournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    status = status,
)