package com.lowbudgetlcs.api.routes.v1.series.dto

import com.lowbudgetlcs.api.routes.v1.series.game.dto.CreateGameDto
import com.lowbudgetlcs.api.routes.v1.series.game.dto.GameDto
import com.lowbudgetlcs.api.routes.v1.series.game.dto.GameResultDto
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.toStage
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId

fun Series.toDto(): SeriesDto =
    SeriesDto(
        id = id.value,
        eventId = eventId.value,
        teamIds = participants.toList().map { it.value },
        totalGames = totalGames,
        eventStage = eventStage,
        result = result?.toDto(),
    )

fun SeriesResult.toDto(): SeriesResultDto = SeriesResultDto(
    winningTeamId = winningTeamId.value,
    losingTeamId = losingTeamId.value,
)


fun NewSeriesDto.toNewSeries(eventId: Int): NewSeries =
    NewSeries(
        eventId = eventId.toEventId(),
        totalGames = totalGames,
        participantIds = Pair(team1Id.toTeamId(), team2Id.toTeamId()),
        eventStage = stage.toStage(),
    )

fun SeriesResultDto.toSeriesResult(seriesId: Int): SeriesResult = SeriesResult(
    seriesId = seriesId.toSeriesId(),
    winningTeamId = this.winningTeamId.toTeamId(),
    losingTeamId = this.losingTeamId.toTeamId(),
)


fun SeriesFilterParams.toQuery(): SeriesQuery =
    SeriesQuery(
        teamIds = teamIds?.map { it.toTeamId() },
        eventStage = stage,
    )


