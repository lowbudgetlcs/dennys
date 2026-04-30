package com.lowbudgetlcs.domain.account.core.model

import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.domain.player.models.types.PlayerId

data class NewAccount(
    val puuid: Puuid,
    val playerId: PlayerId? = null,
)
