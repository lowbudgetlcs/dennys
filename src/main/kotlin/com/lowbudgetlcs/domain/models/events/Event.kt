package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.Team
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

data class EventUpdate(
    val name: String? = null,
    val description: String? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val status: EventStatus? = null
)

fun Event.patch(update: EventUpdate): Event = copy(
    name = update.name ?: this.name,
    description = update.description ?: this.description,
    startDate = update.startDate ?: this.startDate,
    endDate = update.endDate ?: this.endDate,
    status = update.status ?: this.status,
)

data class EventWithTeams(
    val id: EventId,
    val name: String,
    val description: String,
    val eventGroupId: EventGroupId?,
    val tournamentId: TournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val teams: List<Team>
)
