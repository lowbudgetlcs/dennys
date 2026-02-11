package com.lowbudgetlcs.api.dto.accounts

import com.lowbudgetlcs.domain.models.player.account.NewRiotAccount
import com.lowbudgetlcs.domain.models.player.account.RiotAccount
import com.lowbudgetlcs.domain.models.player.account.RiotPuuid

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
