package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.SeriesFilterParams
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toQuery
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toEventStage
import com.lowbudgetlcs.domain.division.core.application.queries.GetEventWithSeriesQuery
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

class GetEventWithSeries(private val query: GetEventWithSeriesQuery) : ApiRoute {
    override fun register(routing: Route) {
        routing.get<EventResources.IdWithSeries> { route ->
            val filter =
                SeriesFilterParams(
                    teamIds = route.teamIds,
                    stage = route.stage?.toEventStage(),
                )
            val event = query.execute(route.eventId.toEventId(), filter.toQuery())
            call.respond(event.toDto())
        }
    }
}
