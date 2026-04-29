package com.lowbudgetlcs.domain.account.dto

import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.toPuuid
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
