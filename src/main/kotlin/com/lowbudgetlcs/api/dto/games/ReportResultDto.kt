package com.lowbudgetlcs.api.dto.games

import kotlinx.serialization.Serializable

@Serializable
data class ReportResultDto(
    val winnerTeamId: Int? = null,
    val loserTeamId: Int? = null,
    val tournamentCodeId: Int? = null,
    val shortcode: String? = null,
)
