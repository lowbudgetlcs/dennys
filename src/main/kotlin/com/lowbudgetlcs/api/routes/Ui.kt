package com.lowbudgetlcs.api.routes

import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.vue
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.uiRoutes() {
    route("/") {
        singlePageApplication {
            vue("/frontend")
        }
    }
}
