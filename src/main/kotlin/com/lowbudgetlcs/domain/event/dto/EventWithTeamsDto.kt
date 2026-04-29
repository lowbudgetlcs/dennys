package com.lowbudgetlcs.domain.event.dto

import com.lowbudgetlcs.api.dto.teams.TeamDto
import com.lowbudgetlcs.domain.event.models.EventStage
import com.lowbudgetlcs.domain.event.models.EventStatus
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
    val eventGroupId: Int? = null,
    val teams: List<TeamDto>,
    val eventStages: Set<EventStage>,
)
