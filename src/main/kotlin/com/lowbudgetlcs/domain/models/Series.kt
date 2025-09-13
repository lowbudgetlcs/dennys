package com.lowbudgetlcs.domain.models

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.team.TeamId

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
    val gamesToWin: Int,
    val participants: List<TeamId>,
    val result: SeriesResult?,
)

data class NewSeries(
    val eventId: EventId,
    val gamesToWin: Int,
    val participantIds: List<TeamId>,
)
