package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class Series(
    val id: SeriesId,
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participants: Pair<TeamId, TeamId>,
    val result: SeriesResult?,
)
