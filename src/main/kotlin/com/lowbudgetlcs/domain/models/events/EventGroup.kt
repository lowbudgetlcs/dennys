package com.lowbudgetlcs.domain.models.events

@JvmInline
value class EventGroupId(val value: Int)

fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)

data class EventGroup(val id: EventGroupId, val name: String)
