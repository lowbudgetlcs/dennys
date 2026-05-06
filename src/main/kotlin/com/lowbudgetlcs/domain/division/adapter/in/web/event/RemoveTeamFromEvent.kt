package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.dto.toDto
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import io.ktor.server.resources.delete
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

class RemoveTeamFromEvent(private val service: EventService) : ApiRoute  {
    override fun register(routing: Route) {
        routing.delete<EventResources.IdWithTeamsId> { route ->
            val event = service.removeTeam(route.eventId.toEventId(), route.teamId.toTeamId())
            call.respond(event.toDto())
        }
    }
}
