package com.lowbudgetlcs.domain.team.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.team.core.models.NewTeam
import com.lowbudgetlcs.domain.team.core.models.types.toTeamName
import kotlinx.serialization.Serializable

@Serializable
data class NewTeamDto(
    val name: String,
    val eventId: Int? = null,
    val logo: String? = null,
)

// Extensions
fun NewTeamDto.toNewTeam(): NewTeam =
    NewTeam(
        name = name.toTeamName(),
        logo = logo,
    )
