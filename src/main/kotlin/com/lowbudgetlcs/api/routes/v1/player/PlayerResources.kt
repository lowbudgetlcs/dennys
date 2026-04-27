package com.lowbudgetlcs.api.routes.v1.player

import io.ktor.resources.Resource

@Resource("/")
class PlayerResources {
    @Resource("{playerId}")
    data class ById(
        val parent: PlayerResources = PlayerResources(),
        val playerId: Int,
    )

    @Resource("{playerId}/teams")
    data class ByIdTeams(
        val parent: PlayerResources = PlayerResources(),
        val playerId: Int,
    )

    @Resource("{playerId}/accounts")
    data class Accounts(
        val parent: PlayerResources = PlayerResources(),
        val playerId: Int,
    )

    @Resource("{playerId}/accounts/{accountId}")
    data class AccountById(
        val parent: PlayerResources = PlayerResources(),
        val playerId: Int,
        val accountId: Int,
    )
}
