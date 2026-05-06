package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

class GetEventWithTeams(private val service: EventService) : ApiRoute {
    override fun register(routing: Route) {
        routing.get<EventResources.IdWithTeams> { route ->
            val events = service.getEventWithTeams(route.eventId.toEventId())
            call.respond(events.toDto())
        }
    }
}
