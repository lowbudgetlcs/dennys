package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName

data class PlayerWithAccounts(
    val id: PlayerId,
    val name: PlayerName,
    val accounts: List<Account>,
)
