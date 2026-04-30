package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class Series(
    val id: SeriesId,
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participants: Pair<TeamId, TeamId>,
    val result: SeriesResult?,
)
