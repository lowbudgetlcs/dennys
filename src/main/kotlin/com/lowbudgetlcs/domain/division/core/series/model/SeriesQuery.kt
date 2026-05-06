package com.lowbudgetlcs.domain.division.core.series.model

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

data class SeriesQuery(
    val teamIds: List<TeamId>?,
    val eventStage: EventStage?,
)

