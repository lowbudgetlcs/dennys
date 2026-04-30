package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.EventUpdate
import com.lowbudgetlcs.domain.event.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.event.core.model.types.toEventName
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
    val status: String? = null,
)

// Extensions
fun PatchEventDto.toEventUpdate(): EventUpdate =
    EventUpdate(
        name = name?.toEventName(),
        description = description?.toEventDescription(),
        startDate = startDate,
        endDate = endDate,
        status = status?.toEventStatus(),
    )
