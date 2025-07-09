package com.lowbudgetlcs.domain.models.event

import java.time.Instant

enum class EventStatus {
    CANCELED, PAUSED, COMPLETED, ACTIVE, NOT_STARTED
}

data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)