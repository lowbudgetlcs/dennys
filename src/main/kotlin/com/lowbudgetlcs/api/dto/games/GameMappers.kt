package com.lowbudgetlcs.api.dto.games

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.toSeriesId

fun CreateGameDto.toNewGame(seriesId: Int): NewGame =
    NewGame(
        seriesId = seriesId.toSeriesId(),
        blueTeamId = blueTeamId.toTeamId(),
        redTeamId = redTeamId.toTeamId(),
    )

fun Game.toDto(): GameDto =
    GameDto(
        id = id.value,
        shortcode = shortcode.value,
        blueTeamId = blueTeamId.value,
        redTeamId = redTeamId.value,
        seriesId = seriesId.value,
        number = number,
    )
