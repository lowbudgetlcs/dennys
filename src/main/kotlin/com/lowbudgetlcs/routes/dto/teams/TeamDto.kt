package com.lowbudgetlcs.routes.dto.teams

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logoName: String?,
    val eventId: Int?
)
