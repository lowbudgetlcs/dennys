package com.lowbudgetlcs.domain.player.core.model

import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName
import com.lowbudgetlcs.domain.team.core.model.Team

data class Player(
    val id: PlayerId,
    val name: PlayerName,
)

// Extensions
fun Player.toPlayerWithTeams(player: Player, teams: List<Team>): PlayerWithTeams = PlayerWithTeams(
    id = id,
    name = name,
    teams = teams
)
