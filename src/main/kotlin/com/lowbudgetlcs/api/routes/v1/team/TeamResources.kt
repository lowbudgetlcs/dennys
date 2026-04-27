package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.domain.event.models.types.EventStatus
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
class TeamResources(
    val name: String? = null,
) {
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
    @Serializable
    @Resource("{teamId}/players/{playerId}")
    data class ByIdPlayersById(
        val parent: TeamResources = TeamResources(),
        val teamId: Int,
        val playerId: Int,
    )
}
