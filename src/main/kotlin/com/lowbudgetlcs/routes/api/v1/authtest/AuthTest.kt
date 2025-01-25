package com.lowbudgetlcs.routes.api.v1.authtest

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authTestRoutes() {
    route("/authtest") {
        get {
            call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}")
        }
    }
}