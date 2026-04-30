package com.lowbudgetlcs.domain.player.core.model

import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName
import com.lowbudgetlcs.domain.team.models.Team

data class PlayerWithTeams(
    val id: PlayerId,
    val name: PlayerName,
    val teams: List<Team>,
)
