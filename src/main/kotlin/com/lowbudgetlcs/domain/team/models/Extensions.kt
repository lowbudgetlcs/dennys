package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamLogoName
import com.lowbudgetlcs.domain.team.models.types.TeamName

// Type Extensions
fun Int.toTeamId(): TeamId = TeamId(this)

fun String.toTeamName(): TeamName = TeamName(this)

fun String.toTeamLogoName(): TeamLogoName = TeamLogoName(this)

// Class Extensions
fun Team.toTeamWithPlayers(players: List<Player>): TeamWithPlayers =
    TeamWithPlayers(
        id = this.id,
        name = this.name,
        logoName = this.logoName,
        eventId = this.eventId,
        players = players,
    )

fun NewTeam.toTeam(
    id: TeamId,
    eventId: EventId? = null,
) = Team(
    id = id,
    logoName = logoName,
    name = name,
    eventId = eventId,
)

fun Team.patch(update: TeamUpdate): Team =
    copy(
        name = update.name ?: this.name,
        logoName = update.logoName ?: this.logoName,
        eventId = when (update.eventId) {
            PatchField.Unset -> this.eventId
            is PatchField.Value -> update.eventId.value
        }
    )
