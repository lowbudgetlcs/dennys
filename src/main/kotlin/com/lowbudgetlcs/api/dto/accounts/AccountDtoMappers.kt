package com.lowbudgetlcs.api.dto.accounts

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.toPuuid

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
