package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.core.application.queries.GetEventWithSeriesQuery
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.series.model.types.toSeriesId
import com.lowbudgetlcs.domain.division.core.series.services.SeriesService
import io.ktor.server.resources.delete
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

// TODO: Move to series endpoints and implement with a usecase
class EventDeleteSeries(private val service: SeriesService, private val query: GetEventWithSeriesQuery) : ApiRoute {
    override fun register(routing: Route) {
        routing.delete<EventResources.IdWithSeriesId> { route ->
            service.removeSeries(route.seriesId.toSeriesId())
            val event = query.execute(route.eventId.toEventId())
            call.respond(event.toDto())
        }

    }
}
