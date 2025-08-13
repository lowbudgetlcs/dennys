package com.lowbudgetlcs.routes.api.v1.event

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.toEventGroupId
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.routes.dto.events.CreateEventDto
import com.lowbudgetlcs.routes.dto.events.toDto
import com.lowbudgetlcs.routes.dto.events.toNewEvent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventRoutesV1(
    eventService: EventService
) {
    get<EventRoutes>{
        logger.info("📩 Received GET on /v1/event")
        val events = eventService.getAllEvents()
        call.respond(events.map { it.toDto() })
    }

    post {
        logger.info("📩 Received post on /v1/event")
        val createEvent = call.receive<CreateEventDto>()
        val group = createEvent.eventGroupId?.toEventGroupId()?.let { eventService.getEventGroupById(it) }
        logger.debug("\uD83D\uDCC2 Deserialized body: ${Json.encodeToString(createEvent)}")
        eventService.createEvent(
            event = createEvent.toNewEvent(), tournament = NewTournament(createEvent.name)
        )?.let { event ->
            call.respond(event.toDto(group))
            logger.info("✅ Successfully created new Event.")
        } ?: call.respond(HttpStatusCode.InternalServerError).also {
            logger.error("❌ Failed to create new Event.")
        }
    }
    get("/{id}") {
        logger.info("📩 Received get on /v1/event/{id}")
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
            "Missing or malformed id", status = HttpStatusCode.BadRequest
        )
        logger.info("\uD83E\uDEF3 Fetching event '${id}'")
        eventService.getEvent(EventId(id))?.let { event ->
            val group = event.eventGroupId?.let { eventService.getEventGroupById(it) }
            call.respond(event.toDto(group))
        } ?: call.respondText("Event not found", status = HttpStatusCode.NotFound)
    }
}