package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

// Type Extensions
fun String.toEventGroupName(): EventGroupName = EventGroupName(this)

fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)

// Class Extensions
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

fun EventGroup.patch(update: EventGroupUpdate): EventGroup =
    EventGroup(
        id = id,
        name = update.name ?: this.name,
    )
