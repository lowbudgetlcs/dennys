package com.lowbudgetlcs.domain.account.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId

data class Account(
    val id: AccountId,
    val puuid: Puuid,
    val playerId: PlayerId?,
)
