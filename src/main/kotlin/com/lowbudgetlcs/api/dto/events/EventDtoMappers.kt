package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.api.dto.series.toDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.EventQuery
import com.lowbudgetlcs.domain.event.models.EventUpdate
import com.lowbudgetlcs.domain.event.models.EventWithSeries
import com.lowbudgetlcs.domain.event.models.EventWithTeams
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.team.models.toTeamId
import com.lowbudgetlcs.domain.team.models.types.TeamId

fun CreateEventDto.toNewEvent(): NewEvent =
    NewEvent(
        name = name.toEventName(),
        description = description.toEventDescription(),
        startDate = startDate,
        endDate = endDate,
        status = status,
        eventStages = eventStages,
    )

fun PatchEventDto.toEventUpdate(): EventUpdate =
    EventUpdate(
        name = name?.toEventName(),
        description = description?.toEventDescription(),
        startDate = startDate,
        endDate = endDate,
        status = status,
    )

fun EventTeamLinkDto.toTeamId(): TeamId = teamId.toTeamId()

fun EventFilterParams.toQuery(): EventQuery =
    EventQuery(
        name = name,
        status = status,
    )

fun Event.toDto(): EventDto =
    EventDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description.value,
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
        description = description.value,
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
        description = description.value,
        status = status,
        series = series.map { s -> s.toDto() },
        eventStages = eventStages,
    )
