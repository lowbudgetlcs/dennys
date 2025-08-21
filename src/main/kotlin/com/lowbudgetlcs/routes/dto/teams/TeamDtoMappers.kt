package com.lowbudgetlcs.routes.dto.teams

import com.lowbudgetlcs.domain.models.NewTeam
import com.lowbudgetlcs.domain.models.Team
import com.lowbudgetlcs.domain.models.TeamLogoName
import com.lowbudgetlcs.domain.models.TeamName

fun NewTeamDto.toNewTeam(): NewTeam =
    NewTeam(
        name = TeamName(name),
        logoName = logoName?.let { TeamLogoName(it) }
    )

fun Team.toDto(): TeamDto =
    TeamDto(
        id = id.value,
        name = name.value,
        logoName = logoName?.value,
        eventId = eventId?.value
    )