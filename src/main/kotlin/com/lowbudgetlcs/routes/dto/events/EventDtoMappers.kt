package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.routes.dto.series.NewSeriesDto
import com.lowbudgetlcs.routes.dto.series.toDto
import com.lowbudgetlcs.routes.dto.teams.toDto

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
    name = name, description = description, startDate = startDate, endDate = endDate, status = status
)

fun NewSeriesDto.toNewSeries(eventId: Int): NewSeries = NewSeries(
    eventId = eventId.toEventId(),
    gamesToWin = gamesToWin,
    participantIds = listOf(team1Id.toTeamId(), team2Id.toTeamId())
)

fun PatchEventDto.toEventUpdate(): EventUpdate = EventUpdate(
    name = name, description = description, startDate = startDate, endDate = endDate, status = status
)

fun EventTeamLinkDto.toTeamId(): TeamId = teamId.toTeamId()

fun Event.toDto(): EventDto = EventDto(
    id = id.value,
    name = name,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    description = description,
    status = status,
    tournamentId = riotTournamentId.value
)

fun EventWithTeams.toDto(): EventWithTeamsDto = EventWithTeamsDto(
    id = id.value,
    name = name,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    description = description,
    status = status,
    tournamentId = riotTournamentId.value,
    teams = teams.map { t -> t.toDto() })

fun EventWithSeries.toDto(): EventWithSeriesDto = EventWithSeriesDto(
    id = id.value,
    name = name,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    description = description,
    status = status,
    riotTournamentId = riotTournamentId.value,
    series = series.map { s -> s.toDto() })
