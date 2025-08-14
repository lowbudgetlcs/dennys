package com.lowbudgetlcs.routes.api.v1.event

import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.routes.dto.events.CreateEventDto
import com.lowbudgetlcs.routes.dto.events.toDto
import com.lowbudgetlcs.routes.dto.events.toNewEvent
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventEndpointsV1(
    eventService: EventService
) {
    post<EventResourcesV1> {
        logger.info("📩 Received POST on /v1/event")
        val dto = call.receive<CreateEventDto>()
        val created = eventService.createEvent(dto.toNewEvent(), NewTournament(dto.name))
        call.respond(HttpStatusCode.Created, created.toDto())
    }
    get<EventResourcesV1> {
        logger.info("📩 Received GET on /v1/event")
        val events = eventService.getAllEvents()
        call.respond(events.map { it.toDto() })
    }
    get<EventResourcesV1.ById> { route ->
        logger.info("📩 Received GET on /v1/event/{id}")
        val event = eventService.getEvent(route.eventId.toEventId())
        call.respond(event.toDto())
    }
}
