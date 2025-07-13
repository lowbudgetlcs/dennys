package com.lowbudgetlcs.domain.models

@JvmInline
value class SeriesId(val value: Int)

data class Series(
    val id: SeriesId,
    val eventId: EventId,
    val gamesToWin: Int,
    val winnerId: TeamId?,
    val loserId: TeamId?
)

data class NewSeries(
    val eventId: EventId,
    val gamesToWin: Int,
    val participantIds: List<TeamId>,
)