package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

class GetEvent(private val service: EventService) : ApiRoute {
    override fun register(routing: Route) {
        routing.get<EventResources.Id> { route ->
            val event = service.getEvent(route.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
}
