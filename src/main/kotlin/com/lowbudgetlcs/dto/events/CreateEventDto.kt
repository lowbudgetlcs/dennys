package com.lowbudgetlcs.dto.events

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class CreateEventDto(
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    @Contextual
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: String
)