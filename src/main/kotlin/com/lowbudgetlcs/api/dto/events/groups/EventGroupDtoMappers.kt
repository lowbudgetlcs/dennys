package com.lowbudgetlcs.api.dto.events.groups

import com.lowbudgetlcs.api.dto.events.toDto
import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupUpdate
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup

fun EventGroup.toDto(): EventGroupDto =
    EventGroupDto(
        id = id.value,
        name = name,
    )

fun CreateEventGroupDto.toNewEventGroup(): NewEventGroup =
    NewEventGroup(
        name = name,
    )

fun PatchEventGroupDto.toEventGroupUpdate(): EventGroupUpdate =
    EventGroupUpdate(
        name = name,
    )

fun EventGroupWithEvents.toDto(): EventGroupWithEventsDto =
    EventGroupWithEventsDto(
        id = id.value,
        name = name,
        events = events.map { it.toDto() },
    )
