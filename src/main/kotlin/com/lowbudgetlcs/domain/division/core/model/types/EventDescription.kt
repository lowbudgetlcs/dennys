package com.lowbudgetlcs.domain.division.core.model.types

const val EVENT_DESCRIPTION_MAX_LENGTH = 10_000

@JvmInline
value class EventDescription(val value: String) {
    init {
        require(value.length <= EVENT_DESCRIPTION_MAX_LENGTH) { "Event description must be less than $EVENT_DESCRIPTION_MAX_LENGTH characters." }
    }
}

// Extensions
fun String.toEventDescription(): EventDescription = EventDescription(this)
