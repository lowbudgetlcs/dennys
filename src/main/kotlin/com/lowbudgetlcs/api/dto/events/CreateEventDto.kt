package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CreateEventDto(
    val name: String,
    val description: String,
    @Contextual
    val startDate: Instant,
    @Contextual
    val endDate: Instant,
    val status: EventStatus,
    val eventGroupId: Int? = null
)
