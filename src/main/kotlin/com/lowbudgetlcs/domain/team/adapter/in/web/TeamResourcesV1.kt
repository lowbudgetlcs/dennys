package com.lowbudgetlcs.domain.team.adapter.`in`.web

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
class TeamResourcesV1(
    val name: String? = null,
) {
    @Serializable
    @Resource("{teamId}")
    data class ById(
        val parent: TeamResourcesV1 = TeamResourcesV1(),
        val teamId: Int,
    )

    @Serializable
    @Resource("{teamId}/players")
    data class ByIdPlayers(
        val parent: TeamResourcesV1 = TeamResourcesV1(),
        val teamId: Int,
    )
    @Serializable
    @Resource("{teamId}/players/{playerId}")
    data class ByIdPlayersById(
        val parent: TeamResourcesV1 = TeamResourcesV1(),
        val teamId: Int,
        val playerId: Int,
    )
}
