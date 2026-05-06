package com.lowbudgetlcs.domain.division.core.model.types

@JvmInline
value class EventId(
    val value: Int,
)

// Extensions
fun Int.toEventId(): EventId = EventId(this)
