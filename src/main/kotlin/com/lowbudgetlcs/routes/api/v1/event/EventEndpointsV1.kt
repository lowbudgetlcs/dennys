package com.lowbudgetlcs.routes.api.v1.event

import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.services.IEventService
import com.lowbudgetlcs.routes.dto.events.*
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventEndpointsV1(
    eventService: IEventService,
    seriesService: ISeriesService
) {
    get<EventResourcesV1> {
        logger.info("📩 Received GET on /v1/event")
        val events = eventService.getAllEvents()
        call.respond(events.map { it.toDto() })
    }
    post<EventResourcesV1> {
        logger.info("📩 Received POST on /v1/event")
        val dto = call.receive<CreateEventDto>()
        val created = eventService.createEvent(dto.toNewEvent(), NewTournament(dto.name))
        call.respond(HttpStatusCode.Created, created.toDto())
    }
    get<EventResourcesV1.ById> { route ->
        logger.info("📩 Received GET on /v1/event/{id}")
        val event = eventService.getEvent(route.eventId.toEventId())
        call.respond(event.toDto())
    }
    patch<EventResourcesV1.ById> { route ->
        logger.info("📩 Received PATCH on /v1/event")
        val dto = call.receive<PatchEventDto>()
        val updated = eventService.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
        call.respond(updated.toDto())
    }
    get<EventResourcesV1.ByIdTeams> { route ->
        logger.info("📩 Received GET on /v1/event/{id}/teams")
        val events = eventService.getEventWithTeams(route.eventId.toEventId())
        call.respond(events.toDto())
    }
    post<EventResourcesV1.ByIdTeams> { route ->
        logger.info("📩 Received POST on /v1/event/{eventId}/teams/{teamId}")
        val team = call.receive<EventTeamLinkDto>()
        val event = eventService.addTeam(route.eventId.toEventId(), team.toTeamId())
        call.respond(event.toDto())
    }
    get<EventResourcesV1.ByIdSeries> { route ->
        logger.info("📩 Received GET on /v1/event/{id}/series")
        val events = eventService.getEventWithSeries(route.eventId.toEventId())
        call.respond(events.toDto())
    }
    post<EventResourcesV1.ByIdSeries> { route ->
        logger.info("📩 Received POST on /v1/event/{eventId}/series")
        val series = call.receive<EventSeriesLinkDto>()
        val event = seriesService.addSeries(route.eventId.toEventId(), series.toSeriesId())
        call.respond(event.toDto())
    }
    delete<EventResourcesV1.ByIdSeries> { route ->
        logger.info("📩 Received DELETE on /v1/event/{eventId}/series/{seriesId}")
        val series = call.receive<EventSeriesLinkDto>()
        val event = seriesService.removeSeries(route.eventId.toEventId(), series.toSeriesId())
        call.respond(event.toDto())
    }
    delete<EventResourcesV1.ByIdTeams> { route ->
        logger.info("📩 Received DELETE on /v1/event/{eventId}/teams/{teamId}")
        val team = call.receive<EventTeamLinkDto>()
        val event = eventService.removeTeam(route.eventId.toEventId(), team.toTeamId())
        call.respond(event.toDto())
    }
}
