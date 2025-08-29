package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.api.dto.series.SeriesDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventWithSeriesDto(
    val id: Int,
    val name: String,
    val description: String,
    @Contextual val createdAt: Instant,
    @Contextual val startDate: Instant,
    @Contextual val endDate: Instant,
    val status: EventStatus,
    val riotTournamentId: Int,
    val series: List<SeriesDto>
)
