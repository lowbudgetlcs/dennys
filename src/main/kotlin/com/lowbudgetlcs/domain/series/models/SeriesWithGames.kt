package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.types.TeamId
import java.time.Instant

data class SeriesWithGames(
    val id: SeriesId,
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participants: Pair<TeamId, TeamId>,
    val result: SeriesResult?,
    val status: SeriesStatus,
    val tournamentCodes: List<TournamentCode>,
    val games: List<Game>,
) {
    val lastCodeIssuedAt: Instant? get() = tournamentCodes.maxOfOrNull { it.createdAt }
    val lastGameAt: Instant? get() = games.maxOfOrNull { it.createdAt }
}
