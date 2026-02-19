package com.lowbudgetlcs.api.dto.events.groups

import com.lowbudgetlcs.api.dto.events.toDto
import com.lowbudgetlcs.domain.models.eventgroup.EventGroup
import com.lowbudgetlcs.domain.models.eventgroup.EventGroupUpdate
import com.lowbudgetlcs.domain.models.eventgroup.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.eventgroup.NewEventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupName
import com.lowbudgetlcs.domain.models.events.toEventId

fun EventGroup.toDto(): EventGroupDto =
    EventGroupDto(
        id = id.value,
        name = name.value,
    )

fun CreateEventGroupDto.toNewEventGroup(): NewEventGroup =
    NewEventGroup(
        name = EventGroupName(name),
        events = eventIds?.map { it.toEventId() },
    )

fun PatchEventGroupDto.toEventGroupUpdate(): EventGroupUpdate =
    EventGroupUpdate(
        name = EventGroupName(name),
    )

fun EventGroupWithEvents.toDto(): EventGroupWithEventsDto =
    EventGroupWithEventsDto(
        id = id.value,
        name = name.value,
        events = events.map { it.toDto() },
    )
