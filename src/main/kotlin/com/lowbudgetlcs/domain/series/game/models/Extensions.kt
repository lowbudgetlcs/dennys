package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId

// Type Extensions
fun Int.toTournamentCodeId(): TournamentCodeId = TournamentCodeId(this)
