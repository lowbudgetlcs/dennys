package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.series.model.SeriesQuery
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
