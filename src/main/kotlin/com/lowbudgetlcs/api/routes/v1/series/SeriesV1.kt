package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.domain.services.game.IGameService
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.seriesRoutesV1(gameService: IGameService) {
    route("/series") {
        install(RequestValidation) {
        }
        seriesEndpointsV1(gameService)
    }
}
