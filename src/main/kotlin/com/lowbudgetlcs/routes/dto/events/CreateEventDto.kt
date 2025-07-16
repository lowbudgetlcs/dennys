@file:UseSerializers(InstantSerializer::class)

package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.routes.dto.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class CreateEventDto(
    val name: String,
    val description: String,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val eventGroupId: Int?
)
