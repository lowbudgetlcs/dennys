package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDto(
    val id: Int,
    val eventId: Int?,
    val teamIds: List<Int>,
    val totalGames: Int,
    val eventStage: EventStage,
    val status: SeriesStatus,
    val winnerTeamId: Int? = null,
    val doubleLoss: Boolean = false,
)
