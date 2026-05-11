package com.lowbudgetlcs.api.dto.teams

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String? = null,
    val eventId: Int?,
)
