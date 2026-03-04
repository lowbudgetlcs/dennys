package com.lowbudgetlcs.api.dto.accounts

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    val riotPuuid: String,
    val playerId: Int?,
)
