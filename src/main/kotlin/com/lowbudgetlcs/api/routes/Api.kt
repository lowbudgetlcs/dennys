package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.routes.v1.account.accountRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.eventRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.group.eventGroupRoutesV1
import com.lowbudgetlcs.api.routes.v1.player.playerRoutesV1
import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.api.routes.v1.team.teamRoutesV1
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.apiRoutes() {

    route("/api/v1") {
        authenticate("auth-session", "auth-token") {
            eventRoutesV1()
            teamRoutesV1()
            playerRoutesV1()
            accountRoutesV1()
            seriesRoutesV1()
            eventGroupRoutesV1()
        }
    }
}
