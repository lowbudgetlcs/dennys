package com.lowbudgetlcs.api.routes.v1.event.dto

import com.lowbudgetlcs.domain.event.models.types.EventStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class PatchEventDto(
    val name: String? = null,
    val description: String? = null,
    @Contextual
    val startDate: Instant? = null,
    @Contextual
    val endDate: Instant? = null,
    val status: EventStatus? = null,
)
