package com.lowbudgetlcs.api.dto.games

import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId

fun CreateGameDto.toNewTournamentCode(seriesId: Int): NewTournamentCode =
    NewTournamentCode(
        seriesId = seriesId.toSeriesId(),
        blueTeamId = blueTeamId.toTeamId(),
        redTeamId = redTeamId.toTeamId(),
    )

fun TournamentCode.toDto(): TournamentCodeDto =
    TournamentCodeDto(
        id = id.value,
        shortcode = shortcode.value,
        seriesId = seriesId.value,
        blueTeamId = blueTeamId.value,
        redTeamId = redTeamId.value,
        createdAt = createdAt,
    )
