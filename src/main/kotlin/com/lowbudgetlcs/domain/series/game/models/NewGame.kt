package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.types.SeriesId

data class NewGame(
    val seriesId: SeriesId,
    val tournamentCodeId: TournamentCodeId?,
    val riotMatchId: String?,
    val result: GameResult?,
)
