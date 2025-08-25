package com.lowbudgetlcs.routes.dto.series

import com.lowbudgetlcs.routes.dto.teams.TeamDto
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDto(val id: Int, val eventId: Int?, val teamIds: List<Int>)
