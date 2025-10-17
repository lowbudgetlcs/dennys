package com.lowbudgetlcs.api.dto.accounts

import com.lowbudgetlcs.domain.models.riot.account.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotPuuid

fun NewAccountDto.toNewRiotAccount(): NewRiotAccount =
    NewRiotAccount(
        riotPuuid = RiotPuuid(riotPuuid),
    )

fun RiotAccount.toDto(): AcountDto =
    AcountDto(
        id = id.value,
        riotPuuid = riotPuuid.value,
        playerId = playerId?.value,
    )
