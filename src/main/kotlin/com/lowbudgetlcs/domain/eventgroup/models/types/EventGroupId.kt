package com.lowbudgetlcs.domain.eventgroup.models.types

@JvmInline
value class EventGroupId(
    val value: Int,
)

// Extensions
fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)
