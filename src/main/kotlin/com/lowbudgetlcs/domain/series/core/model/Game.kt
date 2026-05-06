package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.domain.division.core.model.types.Shortcode
import com.lowbudgetlcs.domain.series.core.model.types.GameId
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class Game(
    val id: GameId,
    val shortcode: Shortcode,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
    val seriesId: SeriesId,
    val number: Int,
    val result: GameResult?,
)
