package com.lowbudgetlcs.domain.eventgroup.models.types

@JvmInline
value class EventGroupName(
    val value: String,
) {
    init {
        require(!value.isBlank()) { "Event group name cannot be blank." }
    }
}
