package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import com.lowbudgetlcs.domain.models.team.Team
import java.time.Instant

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

fun Event.toEventWithTeams(teams: List<Team>): EventWithTeams = EventWithTeams(
    id = id,
    name = name,
    description = description,
    eventGroupId = eventGroupId,
    riotTournamentId = riotTournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    status = status,
    teams = teams
)

fun Event.toEventWithSeries(series: List<Series>): EventWithSeries = EventWithSeries(
    id = id,
    name = name,
    description = description,
    eventGroupId = eventGroupId,
    riotTournamentId = riotTournamentId,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    status = status,
    series = series
)
