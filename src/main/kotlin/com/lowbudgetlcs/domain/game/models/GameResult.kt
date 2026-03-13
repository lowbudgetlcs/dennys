package com.lowbudgetlcs.domain.game.models

import com.lowbudgetlcs.domain.team.models.types.TeamId

data class GameResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
