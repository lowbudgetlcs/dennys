package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.RiotTournamentId
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.team.Team
import java.time.Instant

fun Event.toEventWithTeams(teams: List<Team>): EventWithTeams =
    EventWithTeams(
        id = id,
        name = name,
        description = description,
        eventGroupId = eventGroupId,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        status = status,
        teams = teams,
        eventStages = eventStages,
    )

fun Event.toEventWithSeries(series: List<Series>): EventWithSeries =
    EventWithSeries(
        id = id,
        name = name,
        description = description,
        eventGroupId = eventGroupId,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        status = status,
        series = series,
        eventStages = eventStages,
    )

fun String.toStage(): EventStage =
    try {
        enumValueOf<EventStage>(this)
    } catch (_: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid stage.")
    }

fun Int.toEventId(): EventId = EventId(this)

fun Int.toRiotTournamentId(): RiotTournamentId = RiotTournamentId(this)

fun String.toShortcode(): Shortcode = Shortcode(this)

fun String.toEventName(): EventName = EventName(this)

fun NewEvent.toEvent(
    id: EventId,
    createdAt: Instant,
    riotTournamentId: RiotTournamentId,
): Event =
    Event(
        id = id,
        name = name,
        description = description,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        eventGroupId = null,
        status = status,
        eventStages = eventStages,
    )

fun Event.patch(update: EventUpdate): Event =
    copy(
        name = update.name ?: this.name,
        description = update.description ?: this.description,
        startDate = update.startDate ?: this.startDate,
        endDate = update.endDate ?: this.endDate,
        status = update.status ?: this.status,
        eventGroupId = if (update.eventGroupId.isZero) this.eventGroupId else update.eventGroupId.value,
    )

fun List<Event>.filterByName(query: EventQuery?): List<Event> =
    this.filter { if (query?.name == null) true else it.name.contains(query.name) }

fun List<Event>.filterByStatus(query: EventQuery?): List<Event> =
    this.filter { if (query?.status == null) true else it.status == query.status }
