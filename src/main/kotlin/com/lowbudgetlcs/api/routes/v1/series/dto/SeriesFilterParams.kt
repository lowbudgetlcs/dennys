package com.lowbudgetlcs.api.routes.v1.series.dto

import com.lowbudgetlcs.domain.event.models.types.EventStage

data class SeriesFilterParams(
    val teamIds: List<Int>?,
    val stage: EventStage?,
)
