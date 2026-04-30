package com.lowbudgetlcs.domain.event.models.types

@JvmInline
value class EventId(
    val value: Int,
)

// Extensions
fun Int.toEventId(): EventId = EventId(this)
