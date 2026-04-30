package com.lowbudgetlcs.domain.account.core.model

import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId

data class Account(
    val id: AccountId,
    val puuid: Puuid,
    val playerId: PlayerId?,
)
