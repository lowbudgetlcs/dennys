package com.lowbudgetlcs.domain.division.core.model.types

@JvmInline
value class EventGroupId(
    val value: Int,
)

// Extensions
fun Int.toEventGroupId(): EventGroupId = EventGroupId(this)
