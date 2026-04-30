package com.lowbudgetlcs.domain.account.routes.dto

import com.lowbudgetlcs.domain.account.models.Account
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    val riotPuuid: String,
    val playerId: Int?,
)

// Extensions
fun Account.toDto() =
    AccountDto(
        id = id.value,
        riotPuuid = puuid.value,
        playerId = playerId?.value,
    )
