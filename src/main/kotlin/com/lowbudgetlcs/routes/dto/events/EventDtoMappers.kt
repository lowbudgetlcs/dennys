package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.routes.dto.eventgroup.EventGroupDto

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
    name = name,
    description = description,
    startDate = startDate,
    endDate = endDate,
    status = status
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
    tournamentId = tournamentId.value
)