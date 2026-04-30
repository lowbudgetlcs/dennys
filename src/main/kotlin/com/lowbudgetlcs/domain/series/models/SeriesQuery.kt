package com.lowbudgetlcs.domain.series.models

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.team.core.models.types.TeamId

data class SeriesQuery(
    val teamIds: List<TeamId>?,
    val eventStage: EventStage?,
)
