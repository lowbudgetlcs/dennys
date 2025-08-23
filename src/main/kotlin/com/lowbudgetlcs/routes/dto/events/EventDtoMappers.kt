package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.TeamId
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventUpdate
import com.lowbudgetlcs.domain.models.events.EventWithTeams
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.toTeamId
import com.lowbudgetlcs.routes.dto.teams.toDto

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
    name = name, description = description, startDate = startDate, endDate = endDate, status = status
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
    tournamentId = tournamentId.value
)

fun EventWithTeams.toDto(): EventWithTeamsDto = EventWithTeamsDto(
    id = id.value,
    name = name,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    description = description,
    status = status,
    tournamentId = tournamentId.value,
    teams = teams.map { t -> t.toDto() }
)