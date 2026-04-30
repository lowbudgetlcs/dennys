package com.lowbudgetlcs.domain.team.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.team.core.models.Team
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String? = null,
    val eventId: Int?,
)

// Extensions
fun Team.toDto(): TeamDto =
    TeamDto(
        id = id.value,
        name = name.value,
        logo = logo,
        eventId = eventId?.value,
    )
