package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.api.dto.games.toDto
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.toStage
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.SeriesWithGames
import com.lowbudgetlcs.domain.team.models.toTeamId

fun Series.toDto(): SeriesDto =
    SeriesDto(
        id = id.value,
        eventId = eventId.value,
        teamIds = participants.toList().map { it.value },
        totalGames = totalGames,
        eventStage = eventStage,
        completed = completed,
        completedAt = completedAt,
        reopenedAt = reopenedAt,
    )

fun NewSeriesDto.toNewSeries(eventId: Int): NewSeries =
    NewSeries(
        eventId = eventId.toEventId(),
        totalGames = totalGames,
        participantIds = Pair(team1Id.toTeamId(), team2Id.toTeamId()),
        eventStage = stage.toStage(),
    )

fun SeriesFilterParams.toQuery(): SeriesQuery =
    SeriesQuery(
        teamIds = teamIds?.map { it.toTeamId() },
        eventStage = stage,
        completed = completed,
    )

fun SeriesWithGames.toDto(): SeriesWithGamesDto =
    SeriesWithGamesDto(
        id = id.value,
        eventId = eventId.value,
        teamIds = participants.toList().map { it.value },
        totalGames = totalGames,
        eventStage = eventStage,
        completed = completed,
        completedAt = completedAt,
        reopenedAt = reopenedAt,
        tournamentCodes = tournamentCodes.map { it.toDto() },
        games = games.map { it.toDto() },
        lastCodeIssuedAt = lastCodeIssuedAt,
        lastGameAt = lastGameAt,
    )
