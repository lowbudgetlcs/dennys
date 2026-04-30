package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.team.core.models.types.TeamId
import com.lowbudgetlcs.domain.team.core.models.types.toTeamId
import kotlinx.serialization.Serializable

@Serializable
data class EventTeamLinkDto(
    val teamId: Int,
)

// Extensions
fun EventTeamLinkDto.toTeamId(): TeamId = teamId.toTeamId()
