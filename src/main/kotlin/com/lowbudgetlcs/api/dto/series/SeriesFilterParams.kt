package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus

data class SeriesFilterParams(
    val teamIds: List<Int>?,
    val stage: EventStage?,
    val status: SeriesStatus?,
)
