package com.lowbudgetlcs.routes.login

import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.loginRoutes() {
    routing {
        route("/login") {}
    }
}
