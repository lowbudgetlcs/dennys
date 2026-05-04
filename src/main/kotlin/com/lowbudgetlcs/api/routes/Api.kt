package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.domain.FeatureRoute
import com.lowbudgetlcs.domain.account.adapter.`in`.web.accountRoutesV1
import com.lowbudgetlcs.domain.event.adapter.`in`.web.eventGroupRoutesV1
import com.lowbudgetlcs.domain.player.adapter.`in`.web.playerRoutesV1
import com.lowbudgetlcs.domain.series.adapter.`in`.web.seriesRoutesV1
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import org.koin.ktor.ext.getKoin

fun Routing.apiRoutes() {
    val routes: List<FeatureRoute> = getKoin().getAll<FeatureRoute>()
    route("/api/v1") {
        authenticate("auth-session", "auth-token") {
            routes.forEach { route ->
                route.register(this)
            }
            playerRoutesV1()
            accountRoutesV1()
            seriesRoutesV1()
            eventGroupRoutesV1()
        }
    }
}
