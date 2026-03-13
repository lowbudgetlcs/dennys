package com.lowbudgetlcs.domain.game.models

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class Game(
    val id: GameId,
    val shortcode: Shortcode,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
    val seriesId: SeriesId,
    val number: Int,
    val result: GameResult?,
)
