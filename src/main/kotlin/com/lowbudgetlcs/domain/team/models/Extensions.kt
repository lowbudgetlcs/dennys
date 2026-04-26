package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName

// Type Extensions
fun Int.toTeamId(): TeamId = TeamId(this)

fun String.toTeamName(): TeamName = TeamName(this)

// Class Extensions
fun Team.toTeamWithPlayers(players: List<Player>): TeamWithPlayers =
    TeamWithPlayers(
        id = this.id,
        name = this.name,
        logo = this.logo,
        eventId = this.eventId,
        players = players,
    )

fun Team.patch(update: TeamUpdate): Team =
    copy(
        name = update.name ?: this.name,
        logo =
            when (update.logo) {
                PatchField.Unset -> this.logo
                is PatchField.Value -> update.logo.value
            },
        eventId =
            when (update.eventId) {
                PatchField.Unset -> this.eventId
                is PatchField.Value -> update.eventId.value
            },
    )

fun NewTeam.toTeam(
    id: TeamId,
    eventId: EventId?,
): Team =
    Team(
        id = id,
        name = name,
        logo = logo,
        eventId = eventId,
    )
