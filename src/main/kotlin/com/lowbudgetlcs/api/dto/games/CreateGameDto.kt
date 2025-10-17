package com.lowbudgetlcs.api.dto.games

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameDto(
    val blueTeamId: Int,
    val redTeamId: Int,
)
