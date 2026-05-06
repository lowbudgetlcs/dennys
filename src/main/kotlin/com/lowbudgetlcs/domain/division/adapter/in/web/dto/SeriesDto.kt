package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.series.model.Series
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDto(
    val id: Int,
    val eventId: Int?,
    val teamIds: List<Int>,
    val totalGames: Int,
    val eventStage: EventStage,
)

// Extensions
fun Series.toDto(): SeriesDto =
    SeriesDto(
        id = id.value,
        eventId = eventId.value,
        teamIds = participants.toList().map { it.value },
        totalGames = totalGames,
        eventStage = eventStage,
    )
