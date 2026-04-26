package com.lowbudgetlcs.api.routes.v1.team.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String? = null,
    val eventId: Int?,
)
