package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class GameResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
