package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.team.Team
import java.time.Instant

@JvmInline
value class EventId(
    val value: Int,
)

fun Int.toEventId(): EventId = EventId(this)

enum class EventStatus {
    CANCELED,
    PAUSED,
    COMPLETED,
    ACTIVE,
    NOT_STARTED,
}

enum class Stage {
    REGULAR_SEASON,
    PLAYOFFS,
}

data class Event(
    val id: EventId,
    val name: String,
    val description: String,
    val eventGroupId: EventGroupId?,
    val riotTournamentId: RiotTournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val stages: Set<Stage>,
)

data class EventWithTeams(
    val id: EventId,
    val name: String,
    val description: String,
    val eventGroupId: EventGroupId?,
    val riotTournamentId: RiotTournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val teams: List<Team>,
    val stages: Set<Stage>,
)

data class EventWithSeries(
    val id: EventId,
    val name: String,
    val description: String,
    val eventGroupId: EventGroupId?,
    val riotTournamentId: RiotTournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val series: List<Series>,
    val stages: Set<Stage>,
)
