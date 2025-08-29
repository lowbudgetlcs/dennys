package com.lowbudgetlcs.api.dto.teams

import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.toTeamLogoName
import com.lowbudgetlcs.domain.models.team.toTeamName

fun NewTeamDto.toNewTeam(): NewTeam =
    NewTeam(
        name = name.toTeamName(),
        logoName = logoName?.toTeamLogoName()
    )

fun Team.toDto(): TeamDto =
    TeamDto(
        id = id.value,
        name = name.value,
        logoName = logoName?.value,
        eventId = eventId?.value
    )