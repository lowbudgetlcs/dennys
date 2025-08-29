package com.lowbudgetlcs.domain.models.events.group

import com.lowbudgetlcs.domain.models.events.Event

@JvmInline
value class EventGroupId(val value: Int)

fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)

data class EventGroup(val id: EventGroupId, val name: String)

data class EventGroupWithEvents(val id: EventGroupId, val name: String, val events: List<Event>)

data class NewEventGroup(val name: String)
