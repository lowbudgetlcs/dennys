package com.lowbudgetlcs.routes.dto.accounts

import kotlinx.serialization.Serializable

@Serializable
data class NewRiotAccountDto(
    val riotPuuid: String,
    val playerId: Int? = null
)
