package com.lowbudgetlcs.domain.models

import java.time.OffsetDateTime

enum class EventStatus {
    CANCELED, PAUSED, COMPLETED, ACTIVE, NOT_STARTED
}

data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    val createdAt: OffsetDateTime,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: EventStatus
) {
    fun isActive(): Boolean = status == EventStatus.ACTIVE
}