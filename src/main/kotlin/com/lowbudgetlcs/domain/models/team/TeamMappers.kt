package com.lowbudgetlcs.domain.models.team

import com.lowbudgetlcs.domain.models.player.Player

fun Team.toTeamWithPlayers(players: List<Player>): TeamWithPlayers =
    TeamWithPlayers(
        id = this.id,
        name = this.name,
        logoName = this.logoName,
        eventId = this.eventId,
        players = players,
    )
