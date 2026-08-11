package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.event.models.types.EventStage
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class SeriesDto(
    val id: Int,
    val eventId: Int?,
    val teamIds: List<Int>,
    val totalGames: Int,
    val eventStage: EventStage,
    val completed: Boolean,
    @Contextual
    val completedAt: Instant? = null,
    @Contextual
    val reopenedAt: Instant? = null,
)
