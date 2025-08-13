package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.routes.dto.eventgroup.EventGroupDto

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
    name = name,
    description = description,
    startDate = startDate,
    endDate = endDate,
    eventGroupId = eventGroupId?.let { EventGroupId(it) },
    status = status
)

fun EventGroup.toDto(): EventGroupDto = EventGroupDto(
    id = id.value, name = name
)

fun Event.toDto(group: EventGroup?): EventDto = EventDto(
    id = id.value,
    name = name,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    description = description,
    tournamentId = tournamentId.value,
    eventGroup = group?.toDto(),
    status = status,
)