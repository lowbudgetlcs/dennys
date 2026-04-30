package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.team.core.models.types.TeamId

data class GameResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
