package com.lowbudgetlcs.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

enum class EventStatus {
    CANCELED, PAUSED, COMPLETED, ACTIVE, NOT_STARTED
}

@Serializable
data class Event(
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    @Contextual
    val createdAt: OffsetDateTime,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: EventStatus
)
