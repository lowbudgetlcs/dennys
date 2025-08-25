package com.lowbudgetlcs.routes.dto.accounts

import com.lowbudgetlcs.domain.models.riot.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotPuuid

fun NewAccountDto.toNewRiotAccount(): NewRiotAccount =
    NewRiotAccount(
        riotPuuid = RiotPuuid(riotPuuid)
    )

fun RiotAccount.toDto(): AcountDto =
    AcountDto(
        id = id.value,
        riotPuuid = riotPuuid.value,
        playerId = playerId?.value
    )