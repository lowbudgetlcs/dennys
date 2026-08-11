package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class NewTournamentCode(
    val seriesId: SeriesId,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
)
