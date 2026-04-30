package com.lowbudgetlcs.domain.player.adapter.`in`.web

import io.ktor.resources.Resource

@Resource("/")
class PlayerResourcesV1 {
    @Resource("{playerId}")
    data class ById(
        val parent: PlayerResourcesV1 = PlayerResourcesV1(),
        val playerId: Int,
    )

    @Resource("{playerId}/teams")
    data class ByIdTeams(
        val parent: PlayerResourcesV1 = PlayerResourcesV1(),
        val playerId: Int,
    )

    @Resource("{playerId}/accounts")
    data class Accounts(
        val parent: PlayerResourcesV1 = PlayerResourcesV1(),
        val playerId: Int,
    )

    @Resource("{playerId}/accounts/{accountId}")
    data class AccountById(
        val parent: PlayerResourcesV1 = PlayerResourcesV1(),
        val playerId: Int,
        val accountId: Int,
    )
}
