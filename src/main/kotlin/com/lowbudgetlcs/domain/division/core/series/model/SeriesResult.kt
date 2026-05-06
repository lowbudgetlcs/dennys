package com.lowbudgetlcs.domain.division.core.series.model

import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class SeriesResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
