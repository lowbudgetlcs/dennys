package com.lowbudgetlcs.routes.dto.players

@kotlinx.serialization.Serializable
data class RiotAccountDto(
    val id: Int,
    val riotPuuid: String,
    val playerId: Int?
)
