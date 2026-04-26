package com.lowbudgetlcs.api.routes.v1.series.game.dto

import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId

fun Game.toDto(): GameDto =
    GameDto(
        id = id.value,
        shortcode = shortcode.value,
        blueTeamId = blueTeamId.value,
        redTeamId = redTeamId.value,
        seriesId = seriesId.value,
        number = number,
        result = result?.toDto(),
    )

fun GameResult.toDto(): GameResultDto = GameResultDto(
    winningTeamId = winningTeamId.value,
    losingTeamId = losingTeamId.value
)

fun GameResultDto.toGameResult(gameId: Int): GameResult = GameResult(
    gameId = gameId.toGameId(),
    winningTeamId = winningTeamId.toTeamId(),
    losingTeamId = losingTeamId.toTeamId(),
)

fun CreateGameDto.toNewGame(seriesId: Int): NewGame =
    NewGame(
        seriesId = seriesId.toSeriesId(),
        blueTeamId = blueTeamId.toTeamId(),
        redTeamId = redTeamId.toTeamId(),
    )
