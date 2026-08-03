package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import java.time.Instant

data class Game(
    val id: GameId,
    val seriesId: SeriesId,
    val tournamentCodeId: TournamentCodeId?,
    val riotMatchId: String?,
    val number: Int,
    val createdAt: Instant,
    val result: GameResult?,
)
