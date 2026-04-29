package com.lowbudgetlcs.domain.account.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId

data class NewAccount(
    val puuid: Puuid,
    val playerId: PlayerId? = null,
)
