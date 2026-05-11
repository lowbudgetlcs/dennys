package com.lowbudgetlcs.api.dto.teams

import kotlinx.serialization.Serializable

@Serializable
data class NewTeamDto(
    val name: String,
    val eventId: Int? = null,
    val logo: String? = null,
)
