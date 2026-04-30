package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class NewGame(
    val seriesId: SeriesId,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
)
