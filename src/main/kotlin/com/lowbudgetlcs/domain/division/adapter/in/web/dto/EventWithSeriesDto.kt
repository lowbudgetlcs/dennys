package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.model.EventWithSeries
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.SeriesDto
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.toDto
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
    val status: String,
    val eventGroupId: Int? = null,
    val series: List<SeriesDto>,
    val eventStages: Set<String>,
)

// Extensions
fun EventWithSeries.toDto(): EventWithSeriesDto =
    EventWithSeriesDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description.value,
        status = status.toString(),
        series = series.map { s -> s.toDto() },
        eventStages = eventStages.map { it.toString() }.toSet(),
    )
