package com.lowbudgetlcs.api.dto.events

import kotlinx.serialization.Serializable

@Serializable
data class EventTeamLinkDto(
    val teamId: Int,
)
