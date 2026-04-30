package com.lowbudgetlcs.domain.account.routes.dto

import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.toPuuid
import kotlinx.serialization.Serializable

@Serializable
data class NewAccountDto(
    val riotPuuid: String,
)

// Extensions
fun NewAccountDto.toNewAccount() =
    NewAccount(
        puuid = riotPuuid.toPuuid(),
        playerId = null,
    )
