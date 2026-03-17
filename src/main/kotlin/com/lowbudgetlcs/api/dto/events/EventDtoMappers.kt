package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.api.dto.series.toDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.domain.event.models.*
import com.lowbudgetlcs.domain.team.models.toTeamId
import com.lowbudgetlcs.domain.team.models.types.TeamId

fun CreateEventDto.toNewEvent(): NewEvent =
    NewEvent(
        name = name.toEventName(),
        description = description,
        startDate = startDate,
        endDate = endDate,
        status = status,
        eventStages = eventStages,
    )

fun PatchEventDto.toEventUpdate(): EventUpdate =
    EventUpdate(
        name = name?.toEventName(),
        description = description,
        startDate = startDate,
        endDate = endDate,
        status = status,
    )

fun EventTeamLinkDto.toTeamId(): TeamId = teamId.toTeamId()

fun EventFilterParams.toQuery(): EventQuery =
    EventQuery(
        name = name?.toEventName(),
        status = status,
    )

fun Event.toDto(): EventDto =
    EventDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description,
        status = status,
        eventGroupId = eventGroupId?.value,
        eventStages = eventStages,
    )

fun EventWithTeams.toDto(): EventWithTeamsDto =
    EventWithTeamsDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description,
        status = status,
        teams = teams.map { t -> t.toDto() },
        eventStages = eventStages,
    )

fun EventWithSeries.toDto(): EventWithSeriesDto =
    EventWithSeriesDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description,
        status = status,
        series = series.map { s -> s.toDto() },
        eventStages = eventStages,
    )
