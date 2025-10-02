package com.lowbudgetlcs.api.routes.v1.eventgroup

import com.lowbudgetlcs.domain.services.event.group.IEventGroupService
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.eventRoutesV1(eventGroupService: IEventGroupService) {
    route("/eventGroup") {
        eventGroupEndpointsV1(eventGroupService)
    }
}
