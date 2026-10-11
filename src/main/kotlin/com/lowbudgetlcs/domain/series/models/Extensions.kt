package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.equalsIgnoreOrder

// Type Extensions
fun Int.toSeriesId(): SeriesId = SeriesId(this)

// Filter Extensions
fun List<Series>.filterByStage(query: SeriesQuery?): List<Series> =
    this.filter { if (query?.eventStage == null) true else it.eventStage == query.eventStage }

fun List<Series>.filterByStatus(query: SeriesQuery?): List<Series> =
    this.filter { if (query?.status == null) true else it.status == query.status }

fun List<Series>.filterByParticipants(query: SeriesQuery?): List<Series> {
    val participants = query?.teamIds
    return when {
        participants == null -> this
        participants.isEmpty() -> this
        participants.size == 1 -> this.filter { s -> s.participants.toList().any { participants.contains(it) } }
        else -> this.filter { s -> s.participants.toList().equalsIgnoreOrder(participants) }
    }
}

fun Series.toSeriesWithGames(
    tournamentCodes: List<TournamentCode>,
    games: List<Game>,
): SeriesWithGames =
    SeriesWithGames(
        id = id,
        eventId = eventId,
        eventStage = eventStage,
        totalGames = totalGames,
        participants = participants,
        result = result,
        status = status,
        tournamentCodes = tournamentCodes,
        games = games,
    )
