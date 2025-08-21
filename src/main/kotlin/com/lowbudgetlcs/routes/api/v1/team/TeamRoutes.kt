package com.lowbudgetlcs.routes.api.v1.team

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/team")
class TeamRoutes {

    @Serializable
    @Resource("{teamId}")
    data class ById(val parent: TeamRoutes = TeamRoutes(), val teamId: Int)

}