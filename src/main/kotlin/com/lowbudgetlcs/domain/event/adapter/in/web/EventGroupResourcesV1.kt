package com.lowbudgetlcs.domain.event.adapter.`in`.web

import io.ktor.resources.Resource

@Resource("/")
class EventGroupResourcesV1 {
    @Resource("{eventGroupId}")
    data class ById(
        val parent: EventGroupResourcesV1 = EventGroupResourcesV1(),
        val eventGroupId: Int,
    )

    @Resource("{eventGroupId}/events")
    data class ByIdEvents(
        val parent: EventGroupResourcesV1 = EventGroupResourcesV1(),
        val eventGroupId: Int,
    )

    @Resource("{eventGroupId}/events/{eventId}")
    data class ByIdEventId(
        val parent: EventGroupResourcesV1 = EventGroupResourcesV1(),
        val eventGroupId: Int,
        val eventId: Int,
    )
}
