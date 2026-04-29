package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.event.models.EventStage

data class SeriesFilterParams(
    val teamIds: List<Int>?,
    val stage: EventStage?,
)
