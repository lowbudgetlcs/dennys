package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName
import com.lowbudgetlcs.domain.team.models.Team

data class PlayerWithTeams(
    val id: PlayerId,
    val name: PlayerName,
    val teams: List<Team>,
)
