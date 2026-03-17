package com.lowbudgetlcs.api.dto.teams

import com.lowbudgetlcs.api.dto.UNSET_PATCH_FIELD
import com.lowbudgetlcs.api.dto.players.toDto
import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.team.models.*

fun NewTeamDto.toNewTeam(): NewTeam =
    NewTeam(
        name = name.toTeamName(),
        logo = logo,
    )

fun Team.toDto(): TeamDto =
    TeamDto(
        id = id.value,
        name = name.value,
        logo = logo,
        eventId = eventId?.value,
    )

fun TeamWithPlayers.toDto(): TeamWithPlayersDto = TeamWithPlayersDto(
    id = id.value,
    name = name.value,
    logo = logo,
    eventId = eventId?.value,
    players = players.map { it.toDto() }
)

fun PatchTeamDto.toTeamUpdate(): TeamUpdate =
    TeamUpdate(
        name = name?.toTeamName(),
        logo = when (logo) {
            UNSET_PATCH_FIELD -> {
                PatchField.Unset
            }

            else -> PatchField.Value(logo)
        }
    )
