package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.Event
import com.lowbudgetlcs.domain.models.EventGroup
import com.lowbudgetlcs.domain.models.EventGroupId
import com.lowbudgetlcs.domain.models.NewEvent

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
    name = name,
    description = description,
    startDate = startDate,
    endDate = endDate,
    eventGroupId = eventGroupId?.let { EventGroupId(it) },
    status = status
)

fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)

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