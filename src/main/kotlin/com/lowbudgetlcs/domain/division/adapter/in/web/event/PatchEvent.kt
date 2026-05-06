package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.PatchEventDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toEventUpdate
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import com.lowbudgetlcs.logger
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch

class PatchEvent(private val service: EventService) : ApiRoute {
    override fun register(routing: Route) {
        routing.patch<EventResources.Id> { route ->
            val dto = call.receive<PatchEventDto>()
            this.logger.debug("\uD83C\uDF81 Body: {}", dto)
            val updated = service.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
            call.respond(updated.toDto())
        }
    }
}
