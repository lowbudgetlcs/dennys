package com.lowbudgetlcs.domain.event.routes.dto

import com.lowbudgetlcs.domain.team.models.toTeamId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import kotlinx.serialization.Serializable

@Serializable
data class EventTeamLinkDto(
    val teamId: Int,
)

// Extensions
fun EventTeamLinkDto.toTeamId(): TeamId = teamId.toTeamId()
