package com.lowbudgetlcs.api.dto.teams

import com.lowbudgetlcs.api.dto.players.PlayerDto
import kotlinx.serialization.Serializable

@Serializable
data class TeamWithPlayersDto(
    val id: Int,
    val name: String,
    val logo: String?,
    val eventId: Int?,
    val players: List<PlayerDto>,
)
