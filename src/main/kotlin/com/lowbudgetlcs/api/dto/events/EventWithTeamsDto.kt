package com.lowbudgetlcs.api.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.api.dto.teams.TeamDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventWithTeamsDto(
    val id: Int,
    val name: String,
    val description: String,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val startDate: Instant,
    @Contextual
    val endDate: Instant,
    val status: EventStatus,
    val tournamentId: Int,
    val teams: List<TeamDto>
)
