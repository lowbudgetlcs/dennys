package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toEventStage
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.series.model.NewSeries
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import kotlinx.serialization.Serializable

@Serializable
data class NewSeriesDto(
    val team1Id: Int,
    val team2Id: Int,
    val totalGames: Int,
    val stage: String,
)

// Extensions
fun NewSeriesDto.toNewSeries(eventId: Int): NewSeries =
    NewSeries(
        eventId = eventId.toEventId(),
        totalGames = totalGames,
        participantIds = team1Id.toTeamId() to team2Id.toTeamId(),
        eventStage = stage.toEventStage(),
    )
