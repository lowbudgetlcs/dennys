package com.lowbudgetlcs.domain.models.events.group

import com.lowbudgetlcs.domain.models.events.Event

@JvmInline
value class EventGroupId(
    val value: Int,
)

@JvmInline
value class EventGroupName(
    val value: String,
) {
    init {
        require(!value.isBlank()) { "Event group name cannot be blank." }
    }
}

fun String.toEventGroupName(): EventGroupName = EventGroupName(this)

fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)

data class EventGroup(
    val id: EventGroupId,
    val name: EventGroupName,
)

data class EventGroupWithEvents(
    val id: EventGroupId,
    val name: EventGroupName,
    val events: List<Event>,
)

data class NewEventGroup(
    val name: EventGroupName,
)
