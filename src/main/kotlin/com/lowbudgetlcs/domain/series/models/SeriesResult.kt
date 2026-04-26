package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class SeriesResult(
    val seriesId: SeriesId,
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)
