package com.lowbudgetlcs.domain.models

import java.time.Instant

@JvmInline
value class EventId(val value: Int)

@JvmInline
value class EventGroupId(val value: Int)

data class EventGroup(val id: EventGroupId, val name: String)

data class Event(
    val id: EventId,
    val name: String,
    val description: String,
    val eventGroupId: EventGroupId?,
    val tournamentId: TournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)

data class NewEvent(
    val name: String,
    val description: String,
    val eventGroupId: EventGroupId?,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)

enum class EventStatus {
    CANCELED, PAUSED, COMPLETED, ACTIVE, NOT_STARTED
}
