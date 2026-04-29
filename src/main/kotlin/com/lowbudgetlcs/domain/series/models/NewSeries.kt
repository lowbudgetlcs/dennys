package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.event.models.EventId
import com.lowbudgetlcs.domain.event.models.EventStage
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class NewSeries(
    val eventId: EventId,
    val eventStage: EventStage,
    val totalGames: Int,
    val participantIds: Pair<TeamId, TeamId>,
)
