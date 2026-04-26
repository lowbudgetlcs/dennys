package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class GameResult(
    val gameId: GameId,
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
) {
    init {
        require(winningTeamId != losingTeamId) { "Winning team ID cannot be the same as losingTeamId" }
    }
}
