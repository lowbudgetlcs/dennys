package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.domain.account.adapter.`in`.web.accountRoutesV1
import com.lowbudgetlcs.domain.event.adapter.`in`.web.eventGroupRoutesV1
import com.lowbudgetlcs.domain.event.adapter.`in`.web.eventRoutesV1
import com.lowbudgetlcs.domain.player.adapter.`in`.web.playerRoutesV1
import com.lowbudgetlcs.domain.series.adapter.`in`.web.seriesRoutesV1
import com.lowbudgetlcs.domain.team.adapter.`in`.web.teamRoutesV1
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
