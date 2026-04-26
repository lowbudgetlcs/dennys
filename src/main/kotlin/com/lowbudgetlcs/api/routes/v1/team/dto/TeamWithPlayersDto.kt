package com.lowbudgetlcs.api.routes.v1.team.dto

import com.lowbudgetlcs.api.routes.v1.player.dto.PlayerDto
import kotlinx.serialization.Serializable

@Serializable
data class TeamWithPlayersDto(
    val id: Int,
    val name: String,
    val logo: String?,
    val eventId: Int?,
    val players: List<PlayerDto>,
)
