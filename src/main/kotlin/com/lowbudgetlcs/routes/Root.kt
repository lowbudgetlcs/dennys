package com.lowbudgetlcs.routes

import com.lowbudgetlcs.routes.riot.riotRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Healthcheck route.
 */
fun Application.routes() {
    routing {
        route("/") {
            get {
                call.respondText("WHAT THE FUCK IS UP DENNYS????")
            }
        }
        riotRoutes()
    }
}