package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.tournament.TournamentId
import java.time.Instant

@JvmInline
value class EventId(val value: Int)

fun Int.toEventId(): EventId = EventId(this)

enum class EventStatus {
    CANCELED, PAUSED, COMPLETED, ACTIVE, NOT_STARTED
}

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