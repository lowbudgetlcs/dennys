package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.logger
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import org.koin.ktor.ext.getKoin

fun Routing.apiRoutes() {
    val routes: List<ApiRoute> = getKoin().getAll<ApiRoute>()
    logger.debug("Available routes: {}", routes)
    route("/api/v1") {
        authenticate("auth-session", "auth-token") {
            routes.forEach { route ->
                logger.debug("Registering {}...", route)
                route.register(this)
            }
        }
    }
}
