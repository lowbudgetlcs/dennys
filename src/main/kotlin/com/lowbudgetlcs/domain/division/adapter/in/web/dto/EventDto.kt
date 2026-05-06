package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.model.Event
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventDto(
    val id: Int,
    val name: String,
    val description: String,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val startDate: Instant,
    @Contextual
    val endDate: Instant,
    val status: String,
    val eventGroupId: Int? = null,
    val eventStages: Set<String>,
)

// Extensions
fun Event.toDto(): EventDto =
    EventDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description.value,
        status = status.toString(),
        eventGroupId = eventGroupId?.value,
        eventStages = eventStages.map { it.toString() }.toSet(),
    )
