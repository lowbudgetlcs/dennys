package com.lowbudgetlcs.domain.event.routes.dto

import com.lowbudgetlcs.api.dto.teams.TeamDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.domain.event.models.EventWithTeams
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
    val status: String,
    val eventGroupId: Int? = null,
    val teams: List<TeamDto>,
    val eventStages: Set<String>,
)

// Extensions
fun EventWithTeams.toDto(): EventWithTeamsDto =
    EventWithTeamsDto(
        id = id.value,
        name = name.value,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        description = description.value,
        status = status.toString(),
        teams = teams.map { t -> t.toDto() },
        eventStages = eventStages.map { it.toString() }.toSet(),
    )
