package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.models.Series

fun Series.toDto(): SeriesDto =
    SeriesDto(id = id.value, eventId = eventId.value, teamIds = participants.map { it.value })
