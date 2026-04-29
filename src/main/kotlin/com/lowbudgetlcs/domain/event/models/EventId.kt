package com.lowbudgetlcs.domain.event.models

@JvmInline
value class EventId(
    val value: Int,
)

// Extensions
fun Int.toEventId(): EventId = EventId(this)
