package com.lowbudgetlcs.routes.dto.games

import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int, val shortcode: String, val blueTeamId: Int, val redTeamId: Int, val seriesId: Int, val number: Int
)