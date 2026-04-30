package com.lowbudgetlcs.domain.event.routes

import io.ktor.resources.Resource

@Resource("/")
class EventResources(
    val name: String? = null,
    val status: String? = null,
) {
    @Resource("{eventId}")
    data class ById(
        val parent: EventResources = EventResources(),
        val eventId: Int,
    )

    @Resource("{eventId}/teams")
    data class ByIdTeams(
        val parent: EventResources = EventResources(),
        val eventId: Int,
    )

    @Resource("{eventId}/teams/{teamId}")
    data class ByIdTeamsId(
        val parent: EventResources = EventResources(),
        val eventId: Int,
        val teamId: Int,
    )

    @Resource("{eventId}/series")
    data class ByIdSeries(
        val parent: EventResources = EventResources(),
        val eventId: Int,
        val teamIds: List<Int>? = null,
        val stage: String? = null,
    )

    @Resource("{eventId}/series/{seriesId}")
    data class ByIdSeriesId(
        val parent: EventResources = EventResources(),
        val eventId: Int,
        val seriesId: Int,
    )
}
