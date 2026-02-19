package com.lowbudgetlcs.domain.models.eventgroup.types

@JvmInline
value class EventGroupName(
    val value: String,
) {
    init {
        require(!value.isBlank()) { "Event group name cannot be blank." }
    }
}
