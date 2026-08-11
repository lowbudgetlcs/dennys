package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.event.models.types.EventStage

data class SeriesFilterParams(
    val teamIds: List<Int>?,
    val stage: EventStage?,
    val completed: Boolean?,
)
