package com.lowbudgetlcs.domain.series.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.series.core.model.SeriesQuery
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId

data class SeriesFilterParams(
    val teamIds: List<Int>?,
    val stage: EventStage?,
)

// Extensions
fun SeriesFilterParams.toQuery(): SeriesQuery =
    SeriesQuery(
        teamIds = teamIds?.map { it.toTeamId() },
        eventStage = stage,
    )
