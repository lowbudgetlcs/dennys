package com.lowbudgetlcs.api.routes.v1.team

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
class TeamResources {
    @Serializable
    @Resource("{teamId}")
    data class ById(
        val parent: TeamResources = TeamResources(),
        val teamId: Int,
    )

    @Serializable
    @Resource("{teamId}/players")
    data class ByIdPlayers(
        val parent: TeamResources = TeamResources(),
        val teamId: Int,
    )
}
