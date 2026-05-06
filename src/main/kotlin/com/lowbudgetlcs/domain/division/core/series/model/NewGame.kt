package com.lowbudgetlcs.domain.division.core.series.model

import com.lowbudgetlcs.domain.division.core.series.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class NewGame(
    val seriesId: SeriesId,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
)
