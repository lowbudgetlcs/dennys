package com.lowbudgetlcs.api.dto.games

import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.toTournamentCodeId
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.team.models.toTeamId

fun Game.toDto(): GameDto =
    GameDto(
        id = id.value,
        seriesId = seriesId.value,
        tournamentCodeId = tournamentCodeId?.value,
        riotMatchId = riotMatchId?.value,
        number = number,
        createdAt = createdAt,
        result = result?.let { GameResultDto(it.winningTeamId.value, it.losingTeamId.value) },
    )

fun ReportResultDto.toReportedResult(): ReportedResult =
    ReportedResult(
        winningTeamId = winnerTeamId?.toTeamId(),
        losingTeamId = loserTeamId?.toTeamId(),
        tournamentCodeId = tournamentCodeId?.toTournamentCodeId(),
        shortcode = shortcode?.toShortcode(),
    )
