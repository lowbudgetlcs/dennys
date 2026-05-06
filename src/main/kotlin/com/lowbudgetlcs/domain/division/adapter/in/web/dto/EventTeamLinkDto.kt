package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import kotlinx.serialization.Serializable

@Serializable
data class EventTeamLinkDto(
    val teamId: Int,
)

// Extensions
fun EventTeamLinkDto.toTeamId(): TeamId = teamId.toTeamId()
