package com.lowbudgetlcs.routes.dto.accounts

import kotlinx.serialization.Serializable

@Serializable
data class RiotAccountDto(
    val id: Int,
    val riotPuuid: String,
    val playerId: Int?
)
