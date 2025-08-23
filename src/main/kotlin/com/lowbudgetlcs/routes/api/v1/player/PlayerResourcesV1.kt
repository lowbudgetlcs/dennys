package com.lowbudgetlcs.routes.api.v1.player

import io.ktor.resources.*

@Resource("/")
class PlayerResourcesV1 {
    @Resource("{playerId}")
    data class ById(val parent: PlayerResourcesV1 = PlayerResourcesV1(), val playerId: Int)

    @Resource("{playerId}/accounts")
    data class Accounts(val parent: PlayerResourcesV1 = PlayerResourcesV1(), val playerId: Int)

    @Resource("{playerId}/accounts/{accountId}")
    data class AccountById(
        val parent: PlayerResourcesV1 = PlayerResourcesV1(),
        val playerId: Int,
        val accountId: Int
    )
}