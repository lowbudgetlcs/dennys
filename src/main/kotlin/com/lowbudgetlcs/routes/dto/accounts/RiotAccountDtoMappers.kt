package com.lowbudgetlcs.routes.dto.accounts

import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotAccount
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.domain.models.toPlayerId

fun NewRiotAccountDto.toNewRiotAccount(): NewRiotAccount =
    NewRiotAccount(
        riotPuuid = RiotPuuid(riotPuuid),
        playerId = playerId?.toPlayerId()
    )

fun RiotAccount.toDto(): RiotAccountDto =
    RiotAccountDto(
        id = id.value,
        riotPuuid = riotPuuid.value,
        playerId = playerId?.value
    )