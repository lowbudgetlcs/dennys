package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.domain.series.ISeriesService
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.seriesRoutesV1(seriesService: ISeriesService) {
    route("/series") {
        seriesEndpointsV1(seriesService)
    }
}
