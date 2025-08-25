package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.routes.dto.series.SeriesDto
import java.time.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class EventWithSeriesDto(
        val id: Int,
        val name: String,
        val description: String,
        @Contextual val createdAt: Instant,
        @Contextual val startDate: Instant,
        @Contextual val endDate: Instant,
        val status: EventStatus,
        val tournamentId: Int,
        val series: List<SeriesDto>
)
