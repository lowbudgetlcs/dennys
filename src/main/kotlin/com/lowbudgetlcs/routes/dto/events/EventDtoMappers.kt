package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.routes.dto.eventgroup.EventGroupDto
import com.lowbudgetlcs.routes.dto.teams.toDto

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
    name = name, description = description, startDate = startDate, endDate = endDate, status = status
)

fun PatchEventDto.toEventUpdate(): EventUpdate = EventUpdate(
    name = name, description = description, startDate = startDate, endDate = endDate, status = status
)

fun EventGroup.toDto(): EventGroupDto = EventGroupDto(
    id = id.value, name = name
)

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
    teams = teams.map { t -> t.toDto() }
)