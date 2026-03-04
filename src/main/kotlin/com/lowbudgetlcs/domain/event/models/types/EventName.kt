package com.lowbudgetlcs.domain.event.models.types

data class EventName(
    val value: String,
) {
    fun contains(name: EventName): Boolean = value.contains(name.value)

    init {
        require(value.isNotEmpty()) { "Event name cannot be empty." }
    }

    init {
        require(value.length < 300) { "Event name cannot be more than 300 characters." }
    }
}
