package com.lowbudgetlcs.domain.event.core.model.types

@JvmInline
value class EventGroupId(
    val value: Int,
)

// Extensions
fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)
