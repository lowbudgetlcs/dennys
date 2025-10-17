package com.lowbudgetlcs.domain.models.events.group

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
