package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.series.model.NewGame
import com.lowbudgetlcs.domain.division.core.series.model.types.toSeriesId
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import kotlinx.serialization.Serializable

@Serializable
data class CreateGameDto(
    val blueTeamId: Int,
    val redTeamId: Int,
)

// Extensions
fun CreateGameDto.toNewGame(seriesId: Int): NewGame =
    NewGame(
        seriesId = seriesId.toSeriesId(),
        blueTeamId = blueTeamId.toTeamId(),
        redTeamId = redTeamId.toTeamId(),
    )
