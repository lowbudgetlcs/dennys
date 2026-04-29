package com.lowbudgetlcs.domain.event.models

enum class EventStage {
    REGULAR_SEASON,
    PLAYOFFS,
}

// Extensions
fun String.toStage(): EventStage =
    try {
        enumValueOf<EventStage>(this)
    } catch (_: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid stage.")
    }
