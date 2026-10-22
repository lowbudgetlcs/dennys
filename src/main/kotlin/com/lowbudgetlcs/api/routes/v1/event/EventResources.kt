package com.lowbudgetlcs.api.routes.v1.event

import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import io.ktor.resources.Resource

@Resource("/")
class EventResources {
    // Not on the parent: Ktor decodes a parent's query params for every child route,
    // so a parent `status` would collide with ByIdSeries.status.
    @Resource("")
    data class Search(
        val parent: EventResources = EventResources(),
        val name: String? = null,
        val status: EventStatus? = null,
    )

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
        val stage: EventStage? = null,
        val status: SeriesStatus? = null,
    )

    @Resource("{eventId}/series/{seriesId}")
    data class ByIdSeriesId(
        val parent: EventResources = EventResources(),
        val eventId: Int,
        val seriesId: Int,
    )
}
