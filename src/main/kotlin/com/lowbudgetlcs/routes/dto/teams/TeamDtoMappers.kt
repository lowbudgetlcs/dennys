package com.lowbudgetlcs.routes.dto.teams

import com.lowbudgetlcs.domain.models.Team

fun Team.toDto(): TeamDto = TeamDto(
    id = id.value
)