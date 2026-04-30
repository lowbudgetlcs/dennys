package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

data class EventGroup(
    val id: EventGroupId,
    val name: EventGroupName,
)

// Extensions
fun EventGroup.patch(update: EventGroupUpdate): EventGroup = EventGroup(
    id = id,
    name = update.name ?: this.name,
)

fun EventGroup.toEventGroupWithEvents(events: List<Event>): EventGroupWithEvents = EventGroupWithEvents(
    id = id,
    name = name,
    events = events,
)
