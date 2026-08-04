package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.series.game.models.Game

data class ReportOutcome(
    val game: Game,
    val recorded: Boolean,
)
