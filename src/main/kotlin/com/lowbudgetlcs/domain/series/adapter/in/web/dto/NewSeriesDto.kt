package com.lowbudgetlcs.domain.series.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toEventStage
import com.lowbudgetlcs.domain.event.core.model.types.toEventId
import com.lowbudgetlcs.domain.series.core.model.NewSeries
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
