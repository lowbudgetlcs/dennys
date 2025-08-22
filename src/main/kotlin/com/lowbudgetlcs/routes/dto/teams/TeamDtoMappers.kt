package com.lowbudgetlcs.routes.dto.teams

import com.lowbudgetlcs.domain.models.*

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