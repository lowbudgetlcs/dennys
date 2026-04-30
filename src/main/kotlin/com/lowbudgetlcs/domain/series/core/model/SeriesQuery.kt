package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class SeriesQuery(
    val teamIds: List<TeamId>?,
    val eventStage: EventStage?,
)
