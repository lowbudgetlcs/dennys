package com.lowbudgetlcs.domain.division.core.event.model.types

@JvmInline
value class EventId(
    val value: Int,
)

// Extensions
fun Int.toEventId(): EventId = EventId(this)
