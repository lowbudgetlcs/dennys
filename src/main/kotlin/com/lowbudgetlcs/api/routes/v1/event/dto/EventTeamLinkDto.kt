package com.lowbudgetlcs.api.routes.v1.event.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventTeamLinkDto(
    val teamId: Int,
)
