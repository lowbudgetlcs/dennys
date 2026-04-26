package com.lowbudgetlcs.api.routes.v1.series.game.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int,
    val shortcode: String,
    val blueTeamId: Int,
    val redTeamId: Int,
    val seriesId: Int,
    val number: Int,
    val result: GameResultDto? = null,
)
