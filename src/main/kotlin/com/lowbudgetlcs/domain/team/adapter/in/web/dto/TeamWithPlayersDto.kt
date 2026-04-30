package com.lowbudgetlcs.domain.team.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.PlayerDto
import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.team.core.models.TeamWithPlayers
import kotlinx.serialization.Serializable

@Serializable
data class TeamWithPlayersDto(
    val id: Int,
    val name: String,
    val logo: String?,
    val eventId: Int?,
    val players: List<PlayerDto>,
)

// Extensions
fun TeamWithPlayers.toDto(): TeamWithPlayersDto = TeamWithPlayersDto(
    id = id.value,
    name = name.value,
    logo = logo,
    eventId = eventId?.value,
    players = players.map { it.toDto() }
)
