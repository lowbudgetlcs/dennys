package com.lowbudgetlcs.api.routes.v1.team.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewTeamDto(
    val name: String,
    val eventId: Int? = null,
    val logo: String? = null,
)
