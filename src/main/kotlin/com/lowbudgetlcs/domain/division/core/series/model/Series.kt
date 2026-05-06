package com.lowbudgetlcs.domain.division.core.series.model

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.series.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class Series(
    val id: SeriesId,
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participants: Pair<TeamId, TeamId>,
    val result: SeriesResult?,
)
