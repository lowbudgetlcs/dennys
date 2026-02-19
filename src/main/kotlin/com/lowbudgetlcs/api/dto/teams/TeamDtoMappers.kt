package com.lowbudgetlcs.api.dto.teams

import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamLogoName
import com.lowbudgetlcs.domain.team.models.toTeamName

fun NewTeamDto.toNewTeam(): NewTeam =
    NewTeam(
        name = name.toTeamName(),
        logoName = logoName?.toTeamLogoName(),
    )

fun Team.toDto(): TeamDto =
    TeamDto(
        id = id.value,
        name = name.value,
        logoName = logoName?.value,
        eventId = eventId?.value,
    )
