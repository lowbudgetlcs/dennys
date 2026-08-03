package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.RiotMatchId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId

// Type Extensions
fun Int.toTournamentCodeId(): TournamentCodeId = TournamentCodeId(this)

fun Int.toGameId(): GameId = GameId(this)

fun String.toRiotMatchId(): RiotMatchId = RiotMatchId(this)
