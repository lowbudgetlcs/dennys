package com.lowbudgetlcs.api.routes.v1.event.group

import io.ktor.resources.Resource

@Resource("/")
class EventGroupResources {
    @Resource("{eventGroupId}")
    data class ById(
        val parent: EventGroupResources = EventGroupResources(),
        val eventGroupId: Int,
    )

    @Resource("{eventGroupId}/events")
    data class ByIdEvents(
        val parent: EventGroupResources = EventGroupResources(),
        val eventGroupId: Int,
    )

    @Resource("{eventGroupId}/events/{eventId}")
    data class ByIdEventId(
        val parent: EventGroupResources = EventGroupResources(),
        val eventGroupId: Int,
        val eventId: Int,
    )
}
