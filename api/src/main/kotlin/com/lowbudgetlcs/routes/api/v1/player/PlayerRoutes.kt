package com.lowbudgetlcs.routes.api.v1.player

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/player")
class PlayerRoutes {

    @Serializable
    @Resource("{playerId}")
    data class ById(val parent: PlayerRoutes = PlayerRoutes(), val playerId: Int)

    @Serializable
    @Resource("{playerId}/accounts")
    data class Accounts(val parent: PlayerRoutes = PlayerRoutes(), val playerId: Int)

    @Serializable
    @Resource("{playerId}/accounts/{accountId}")
    data class AccountById(
        val parent: PlayerRoutes = PlayerRoutes(),
        val playerId: Int,
        val accountId: Int
    )
}