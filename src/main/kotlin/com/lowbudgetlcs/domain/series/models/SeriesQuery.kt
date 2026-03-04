package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class SeriesQuery(
    val teamIds: List<TeamId>?,
    val eventStage: EventStage?,
)
