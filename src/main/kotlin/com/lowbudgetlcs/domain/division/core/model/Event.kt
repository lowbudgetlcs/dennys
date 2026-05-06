package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.model.types.EventDescription
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.division.core.model.types.EventName
import com.lowbudgetlcs.domain.series.core.model.Series
import com.lowbudgetlcs.domain.team.core.model.Team
import java.time.Instant

data class Event(
    val id: EventId,
    val name: EventName,
    val description: EventDescription,
    val eventGroupId: EventGroupId?,
    val riotTournamentId: RiotTournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val eventStages: Set<EventStage>,
)

// Extensions
fun Event.patch(update: EventUpdate): Event = copy(
    name = update.name ?: this.name,
    description = update.description ?: this.description,
    startDate = update.startDate ?: this.startDate,
    endDate = update.endDate ?: this.endDate,
    status = update.status ?: this.status,
    eventGroupId = when (update.eventGroupId) {
        PatchField.Unset -> this.eventGroupId
        is PatchField.Value -> update.eventGroupId.value
    },
)

fun Event.toEventWithTeams(teams: List<Team>): EventWithTeams = EventWithTeams(
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

fun Event.toEventWithSeries(series: List<Series>): EventWithSeries = EventWithSeries(
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
