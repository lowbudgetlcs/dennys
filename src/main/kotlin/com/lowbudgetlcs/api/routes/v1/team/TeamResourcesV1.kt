package com.lowbudgetlcs.api.routes.v1.team

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
class TeamResourcesV1 {
    @Serializable
    @Resource("{teamId}")
    data class ById(
        val parent: TeamResourcesV1 = TeamResourcesV1(),
        val teamId: Int,
    )
}
