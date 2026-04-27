package com.lowbudgetlcs.domain.event.models.types

const val EVENT_NAME_MAX_LENGTH = 120
const val EVENT_NAME_MIN_LENGTH = 3

data class EventName(
    val value: String,
) {
    fun contains(other: EventName): Boolean = value.uppercase().contains(other.value.uppercase())
    fun contains(other: String): Boolean = value.uppercase().contains(other.uppercase())

    init {
        require(value.isNotEmpty()) { "Event name cannot be empty." }
        require(value.length <= EVENT_NAME_MAX_LENGTH) { "Event name must not exceed $EVENT_NAME_MAX_LENGTH characters." }
        require(value.length >= EVENT_NAME_MIN_LENGTH) { "Event name must be at least $EVENT_NAME_MIN_LENGTH characters." }
    }

}
