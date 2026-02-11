package com.lowbudgetlcs.domain.models.player.account

import com.lowbudgetlcs.domain.models.player.PlayerId

@JvmInline
value class AccountId(
    val value: Int,
)

@JvmInline
value class Puuid(
    val value: String,
) {
    init {
        require(value.length == 78) { "Puuids must be 78 characters" }
    }
}

data class Account(
    val id: AccountId,
    val puuid: Puuid,
    val playerId: PlayerId?,
)

data class NewAccount(
    val puuid: Puuid,
    val playerId: PlayerId? = null,
)
