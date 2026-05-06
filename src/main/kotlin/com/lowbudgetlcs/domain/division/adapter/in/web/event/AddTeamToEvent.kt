package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.EventTeamLinkDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toTeamId
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import com.lowbudgetlcs.logger
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

class AddTeamToEvent(private val service: EventService) : ApiRoute {
    override fun register(routing: Route) {
        routing.post<EventResources.IdWithTeams> { route ->
            val dto = call.receive<EventTeamLinkDto>()
            this.logger.debug(dto.toString())
            val event = service.addTeam(route.eventId.toEventId(), dto.toTeamId())
            call.respond(event.toDto())
        }
    }
}
