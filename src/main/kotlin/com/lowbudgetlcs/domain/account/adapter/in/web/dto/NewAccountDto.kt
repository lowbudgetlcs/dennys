package com.lowbudgetlcs.domain.account.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.toPuuid
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
