package com.lowbudgetlcs.dto.events

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class EventDto(
    val id: Int,
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    @Contextual val createdAt: OffsetDateTime,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: String
)