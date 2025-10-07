package com.lowbudgetlcs.api.routes.v1.event.group

import com.lowbudgetlcs.domain.services.event.group.IEventGroupService
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.eventGroupRoutesV1(eventGroupService: IEventGroupService) {
    route("/eventGroup") {
        eventGroupEndpointsV1(eventGroupService)
    }
}
