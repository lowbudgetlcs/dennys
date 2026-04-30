package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.team.core.models.types.TeamId

data class SeriesResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
