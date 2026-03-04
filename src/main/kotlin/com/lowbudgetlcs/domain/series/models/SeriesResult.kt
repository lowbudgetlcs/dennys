package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.team.models.types.TeamId

data class SeriesResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
