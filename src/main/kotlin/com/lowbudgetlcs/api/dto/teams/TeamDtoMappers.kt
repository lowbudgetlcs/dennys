package com.lowbudgetlcs.api.dto.teams

import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.toTeamName

fun NewTeamDto.toNewTeam(): NewTeam =
    NewTeam(
        name = name.toTeamName(),
    )

fun Team.toDto(logoBucketUrl: String): TeamDto =
    TeamDto(
        id = id.value,
        name = name.value,
        logo = "$logoBucketUrl/$logoKey",
        eventId = eventId?.value,
    )

fun PatchTeamDto.toTeamUpdate(): TeamUpdate =
    TeamUpdate(
        name = name?.toTeamName(),
    )
