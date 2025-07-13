package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.domain.models.EventId
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.dto.events.CreateEventDto
import com.lowbudgetlcs.dto.events.toDto
import com.lowbudgetlcs.dto.events.toNewEvent
import com.lowbudgetlcs.dto.events.toNewTournament
import com.lowbudgetlcs.repositories.jooq.JooqEventRepository
import com.lowbudgetlcs.repositories.riot.RiotTournamentRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)
private val eventService: EventService =
    EventService(JooqEventRepository(Database.dslContext), RiotTournamentRepository())

fun Route.eventRoutesV1() {
    route("/event") {
        get("/{id}") {
            logger.info("📩 Received get on /event-v1")
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            logger.info("\uD83E\uDEF3 Fetching event '${id}'")
            eventService.getEvent(EventId(id))?.let { event ->
                call.respond(event.toDto())
            } ?: call.respondText("Event not found.", status = HttpStatusCode.NotFound)
        }
        post {
            logger.info("📩 Received post on /event-v1")
            val createEvent = call.receive<CreateEventDto>()
            logger.debug("\uD83D\uDCC2 Deserialized body: ${Json.encodeToString(createEvent)}")
            eventService.create(
                event = createEvent.toNewEvent(),
                tournament = createEvent.toNewTournament()
            )?.let { event ->
                call.respond(event.toDto())
                logger.info("✅ Successfully created new Event.")
            } ?: call.respond(HttpStatusCode.InternalServerError).also {
                logger.error("❌ Failed to create new Event.")
            }
        }
    }
}