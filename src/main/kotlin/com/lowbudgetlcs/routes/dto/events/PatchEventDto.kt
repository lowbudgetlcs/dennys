package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class PatchEventDto(
    val name: String?,
    val description: String?,
    val startDate: Instant?,
    val endDate: Instant?,
    val status: EventStatus?
)