package com.lowbudgetlcs.api.routes.auth

import com.lowbudgetlcs.domain.services.user.IUserService
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.authRoutes(userService: IUserService) {
    route("/auth") {
        authEndpoints(userService = userService)
    }
}
