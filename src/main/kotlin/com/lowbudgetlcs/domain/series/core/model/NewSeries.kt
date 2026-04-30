package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class NewSeries(
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participantIds: Pair<TeamId, TeamId>,
)
