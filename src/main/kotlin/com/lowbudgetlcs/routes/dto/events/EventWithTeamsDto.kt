package com.lowbudgetlcs.routes.dto.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.routes.dto.teams.TeamDto
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventWithTeamsDto(
    val id: Int,
    val name: String,
    val description: String,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val tournamentId: Int,
    val teams: List<TeamDto>
)
