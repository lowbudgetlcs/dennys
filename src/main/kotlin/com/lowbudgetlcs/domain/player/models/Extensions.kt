package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName
import com.lowbudgetlcs.domain.team.models.Team

// Type Extensions
fun Int.toPlayerId(): PlayerId = PlayerId(this)

fun String.toPlayerName(): PlayerName = PlayerName(this)

// Class Extensions
fun NewPlayer.toPlayer(id: PlayerId): Player =
    Player(
        id = id,
        name = name,
    )

fun Player.toPlayerWithTeams(player: Player, teams: List<Team>): PlayerWithTeams = PlayerWithTeams(
    id = id,
    name = name,
    teams = teams
)
