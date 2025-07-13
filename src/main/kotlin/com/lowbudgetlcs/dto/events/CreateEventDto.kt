@file:UseSerializers(InstantSerializer::class)

package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.EventStatus
import com.lowbudgetlcs.domain.models.MapType
import com.lowbudgetlcs.domain.models.PickType
import com.lowbudgetlcs.dto.InstantSerializer
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
    val metadata: String,
    val pickType: PickType,
    val mapType: MapType
)
