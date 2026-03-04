package com.lowbudgetlcs.api.dto.events.groups

import com.lowbudgetlcs.api.dto.events.toDto
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.eventgroup.models.EventGroup
import com.lowbudgetlcs.domain.eventgroup.models.EventGroupUpdate
import com.lowbudgetlcs.domain.eventgroup.models.EventGroupWithEvents
import com.lowbudgetlcs.domain.eventgroup.models.NewEventGroup
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

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
