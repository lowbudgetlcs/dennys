package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.CreateEventDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toNewEvent
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import com.lowbudgetlcs.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

class CreateEvent(private val service: EventService) : ApiRoute {
    override fun register(routing: Route) {
        routing.post<EventResources.Base> { route ->
            val dto = call.receive<CreateEventDto>()
            this.logger.debug(dto.toString())
            val created = service.createEvent(dto.toNewEvent())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
}
