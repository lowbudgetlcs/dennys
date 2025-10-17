package com.lowbudgetlcs.api.routes.v1.event

import io.ktor.resources.Resource

@Resource("/")
class EventResourcesV1 {
    @Resource("{eventId}")
    data class ById(
        val parent: EventResourcesV1 = EventResourcesV1(),
        val eventId: Int,
    )

    @Resource("{eventId}/teams")
    data class ByIdTeams(
        val parent: EventResourcesV1 = EventResourcesV1(),
        val eventId: Int,
    )

    @Resource("{eventId}/teams/{teamId}")
    data class ByIdTeamsId(
        val parent: EventResourcesV1 = EventResourcesV1(),
        val eventId: Int,
        val teamId: Int,
    )

    @Resource("{eventId}/series")
    data class ByIdSeries(
        val parent: EventResourcesV1 = EventResourcesV1(),
        val eventId: Int,
    )

    @Resource("{eventId}/series/{seriesId}")
    data class ByIdSeriesId(
        val parent: EventResourcesV1 = EventResourcesV1(),
        val eventId: Int,
        val seriesId: Int,
    )
}
