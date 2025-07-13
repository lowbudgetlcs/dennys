package com.lowbudgetlcs.domain.models

import java.time.Instant

@JvmInline
value class EventId(val value: Int)

enum class EventStatus {
    CANCELED, PAUSED, COMPLETED, ACTIVE, NOT_STARTED
}

data class Event(
    val id: EventId,
    val name: String,
    val description: String,
    val riotTournamentId: TournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)

data class NewEvent(
    val name: String,
    val description: String,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)