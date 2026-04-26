package com.lowbudgetlcs.api.routes.v1.series.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeriesResultDto(val winningTeamId: Int, val losingTeamId: Int)
