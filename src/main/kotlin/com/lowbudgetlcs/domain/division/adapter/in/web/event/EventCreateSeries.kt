package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.NewSeriesDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toNewSeries
import com.lowbudgetlcs.domain.division.core.series.services.SeriesService
import com.lowbudgetlcs.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

// TODO: Implement with a usecase and move to series endpoints
class EventCreateSeries(private val service: SeriesService) : ApiRoute {
    override fun register(routing: Route) {
        routing.post<EventResources.IdWithSeries> { route ->
            val dto = call.receive<NewSeriesDto>()
            this.logger.debug(dto.toString())
            val series = service.createSeries(dto.toNewSeries(route.eventId))
            call.respond(HttpStatusCode.Created, series.toDto())
        }
    }
}
