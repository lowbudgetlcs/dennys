package com.lowbudgetlcs.api.dto.accounts

import com.lowbudgetlcs.domain.models.player.account.Account
import com.lowbudgetlcs.domain.models.player.account.NewAccount
import com.lowbudgetlcs.domain.models.player.account.toPuuid

fun NewAccountDto.toNewAccount() =
    NewAccount(
        puuid = riotPuuid.toPuuid(),
        playerId = null,
    )

fun Account.toDto() =
    AccountDto(
        id = id.value,
        riotPuuid = puuid.value,
        playerId = playerId?.value,
    )
