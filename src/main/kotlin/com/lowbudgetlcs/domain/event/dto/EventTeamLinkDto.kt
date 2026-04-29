package com.lowbudgetlcs.domain.event.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventTeamLinkDto(
    val teamId: Int,
)
