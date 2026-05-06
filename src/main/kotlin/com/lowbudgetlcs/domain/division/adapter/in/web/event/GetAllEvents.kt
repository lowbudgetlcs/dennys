package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.EventFilterParams
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toQuery
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

class GetAllEvents(private val service: EventService) : ApiRoute {
    override fun register(routing: Route) {
        routing.get<EventResources.Base> { route ->
            val filter = EventFilterParams(
                name = route.name,
                status = route.status,
            )
            val events = service.getAllEvents(filter.toQuery())
            call.respond(events.map { it.toDto() })
        }
    }
}
