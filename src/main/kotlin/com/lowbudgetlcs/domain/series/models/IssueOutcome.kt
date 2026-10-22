package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.series.game.models.TournamentCode

data class IssueOutcome(
    val code: TournamentCode,
    val issued: Boolean,
)
