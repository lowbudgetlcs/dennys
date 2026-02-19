package com.lowbudgetlcs.domain.models

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.Stage
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
    val stage: Stage,
    val totalGames: Int,
    val participants: List<TeamId>,
    val result: SeriesResult?,
)

data class SeriesFilter(
    val teamIds: List<TeamId>?,
    val stage: Stage?,
)

data class NewSeries(
    val eventId: EventId,
    val stage: Stage,
    val totalGames: Int,
    val participantIds: List<TeamId>,
)

fun List<Series>.filterByStage(stage: Stage?): List<Series> =
    this.filter { if (stage == null) true else it.stage == stage }

fun List<Series>.filterByParticipants(participants: List<TeamId>?): List<Series> =
    when {
        participants == null -> this
        participants.isEmpty() -> this
        participants.size == 1 -> this.filter { s -> s.participants.any { participants.contains(it) } }
        else -> this.filter { s -> s.participants.equalsIgnoreOrder(participants) }
    }
