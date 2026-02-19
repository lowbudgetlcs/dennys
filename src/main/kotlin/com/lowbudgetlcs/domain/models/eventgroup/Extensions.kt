package com.lowbudgetlcs.domain.models.eventgroup

import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupId
import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupName
import com.lowbudgetlcs.domain.models.events.Event

fun NewEventGroup.toEventGroup(id: EventGroupId): EventGroup =
    EventGroup(
        id = id,
        name = name,
    )

fun EventGroup.toEventGroupWithEvents(events: List<Event>): EventGroupWithEvents =
    EventGroupWithEvents(
        id = id,
        name = name,
        events = events,
    )

fun String.toEventGroupName(): EventGroupName = EventGroupName(this)

fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)

fun EventGroup.patch(update: EventGroupUpdate): EventGroup =
    EventGroup(
        id = id,
        name = update.name ?: this.name,
    )
