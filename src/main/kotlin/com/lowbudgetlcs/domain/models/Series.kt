package com.lowbudgetlcs.domain.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.equalsIgnoreOrder

@JvmInline
value class SeriesId(
    val value: Int,
)

fun Int.toSeriesId(): SeriesId = SeriesId(this)

data class SeriesResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)

data class Series(
    val id: SeriesId,
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participants: List<TeamId>,
    val result: SeriesResult?,
)

data class SeriesQuery(
    val teamIds: List<TeamId>?,
    val eventStage: EventStage?,
)

data class NewSeries(
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participantIds: List<TeamId>,
)

fun List<Series>.filterByStage(query: SeriesQuery?): List<Series> =
    this.filter { if (query?.eventStage == null) true else it.eventStage == query.eventStage }

fun List<Series>.filterByParticipants(query: SeriesQuery?): List<Series> {
    val participants = query?.teamIds
    return when {
        participants == null -> this
        participants.isEmpty() -> this
        participants.size == 1 -> this.filter { s -> s.participants.any { participants.contains(it) } }
        else -> this.filter { s -> s.participants.equalsIgnoreOrder(participants) }
    }
}
