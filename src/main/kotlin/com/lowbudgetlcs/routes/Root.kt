package com.lowbudgetlcs.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Healthcheck route.
 */
fun Application.rootRoutes() {
    routing {
        route("/") {
            get {
                call.respondText("WHAT THE FUCK IS UP DENNYS????")
            }
        }
    }
}