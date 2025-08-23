package com.lowbudgetlcs.routes.api.v1.event

import io.ktor.resources.*

@Resource("/")
class EventResourcesV1 {
    @Resource("{eventId}")
    data class ById(val parent: EventResourcesV1 = EventResourcesV1(), val eventId: Int)

    @Resource("{eventId}/teams")
    data class ByIdTeams(val parent: EventResourcesV1 = EventResourcesV1(), val eventId: Int)
}