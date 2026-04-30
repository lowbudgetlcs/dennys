package com.lowbudgetlcs.domain.team.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.team.core.models.TeamQuery

data class TeamFilterParams(
    val name: String?,
)

// Extensions
fun TeamFilterParams.toQuery(): TeamQuery =
    TeamQuery(
        name = name
    )
