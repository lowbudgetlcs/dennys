package com.lowbudgetlcs.domain.team.core.model

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName

data class Team(
    val id: TeamId,
    val name: TeamName,
    val logo: String?,
    val eventId: EventId?,
)

// Extensions
fun Team.patch(update: TeamUpdate): Team = copy(
    name = update.name ?: this.name,
    logo = when (update.logo) {
        PatchField.Unset -> this.logo
        is PatchField.Value -> update.logo.value
    },
    eventId = when (update.eventId) {
        PatchField.Unset -> this.eventId
        is PatchField.Value -> update.eventId.value
    },
)

fun Team.toTeamWithPlayers(players: List<Player>): TeamWithPlayers = TeamWithPlayers(
    id = this.id,
    name = this.name,
    logo = this.logo,
    eventId = this.eventId,
    players = players,
)
