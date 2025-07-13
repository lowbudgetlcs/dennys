@file:UseSerializers(InstantSerializer::class)

package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.EventStatus
import com.lowbudgetlcs.dto.InstantSerializer
import kotlinx.serialization.UseSerializers
import java.time.Instant

data class EventDto(
    val id: Int,
    val name: String,
    val description: String,
    val tournamentId: Int,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus
)