package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.core.models.types.TeamId

data class NewGame(
    val seriesId: SeriesId,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
)
