package com.lowbudgetlcs.domain.eventgroup.models.types

const val EVENT_GROUP_NAME_MAX_LENGTH = 50
const val EVENT_GROUP_NAME_MIN_LENGTH = 3

@JvmInline
value class EventGroupName(
    val value: String,
) {
    init {
        require(!value.isBlank()) { "Event group name cannot be blank." }
        require(value.length <= EVENT_GROUP_NAME_MAX_LENGTH) { "Event group name cannot exceed $EVENT_GROUP_NAME_MAX_LENGTH characters." }
        require(value.length >= EVENT_GROUP_NAME_MIN_LENGTH) { "Event group name must be at least $EVENT_GROUP_NAME_MAX_LENGTH characters." }
    }
}

// Extensions
fun String.toEventGroupName(): EventGroupName = EventGroupName(this)
