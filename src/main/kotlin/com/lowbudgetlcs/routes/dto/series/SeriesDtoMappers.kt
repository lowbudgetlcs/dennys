package com.lowbudgetlcs.routes.dto.series

import com.lowbudgetlcs.domain.models.Series

fun Series.toDto(): SeriesDto = SeriesDto(id = id.value, eventId = eventId.value, teams = listOf())
