package com.lowbudgetlcs.api.routes.v1.series.game.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameDto(
    val blueTeamId: Int,
    val redTeamId: Int,
)
