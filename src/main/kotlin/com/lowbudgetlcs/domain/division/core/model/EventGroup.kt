package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupName

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
