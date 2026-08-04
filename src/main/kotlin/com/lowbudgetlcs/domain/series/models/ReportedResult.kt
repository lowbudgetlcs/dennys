package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class ReportedResult(
    val winningTeamId: TeamId?,
    val losingTeamId: TeamId?,
    val tournamentCodeId: TournamentCodeId?,
    val shortcode: Shortcode?,
)
