package com.lowbudgetlcs.api.routes.v1.event

import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.toSeriesId
import com.lowbudgetlcs.domain.services.IEventService
import com.lowbudgetlcs.domain.services.ISeriesService
import com.lowbudgetlcs.api.dto.events.*
import com.lowbudgetlcs.api.dto.series.NewSeriesDto
import com.lowbudgetlcs.api.dto.series.toDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("EventEndpointsV1")

fun Route.eventEndpointsV1(eventService: IEventService, seriesService: ISeriesService) {
    get<EventResourcesV1> {
        logger.info("📩 Received GET on /v1/event")
        val events = eventService.getAllEvents()
        call.respond(events.map { it.toDto() })
    }
    post<EventResourcesV1> {
        logger.info("📩 Received POST on /v1/event")
        val dto = call.receive<CreateEventDto>()
        logger.debug("Body: {}", dto)
        val created = eventService.createEvent(dto.toNewEvent())
        call.respond(HttpStatusCode.Created, created.toDto())
    }
    get<EventResourcesV1.ById> { route ->
        logger.info("📩 Received GET on /v1/event/${route.eventId}")
        val event = eventService.getEvent(route.eventId.toEventId())
        call.respond(event.toDto())
    }
    patch<EventResourcesV1.ById> { route ->
        logger.info("📩 Received PATCH on /v1/event/${route.eventId}")
        val dto = call.receive<PatchEventDto>()
        logger.debug("Body: {}", dto)
        val updated = eventService.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
        call.respond(updated.toDto())
    }
    get<EventResourcesV1.ByIdTeams> { route ->
        logger.info("📩 Received GET on /v1/event/${route.eventId}/teams")
        val events = eventService.getEventWithTeams(route.eventId.toEventId())
        call.respond(events.toDto())
    }
    post<EventResourcesV1.ByIdTeams> { route ->
        logger.info("📩 Received POST on /v1/event/${route.eventId}/teams")
        val dto = call.receive<EventTeamLinkDto>()
        logger.debug("Body: {}", dto)
        val event = eventService.addTeam(route.eventId.toEventId(), dto.toTeamId())
        call.respond(event.toDto())
    }
    get<EventResourcesV1.ByIdSeries> { route ->
        logger.info("📩 Received GET on /v1/event/${route.eventId}/series")
        val event = eventService.getEventWithSeries(route.eventId.toEventId())
        call.respond(event.toDto())
    }
    post<EventResourcesV1.ByIdSeries> { route ->
        logger.info("📩 Received POST on /v1/event/${route.eventId}/series")
        val dto = call.receive<NewSeriesDto>()
        logger.debug("Body: {}", dto)
        val series = seriesService.createSeries(dto.toNewSeries(route.eventId))
        call.respond(HttpStatusCode.Created, series.toDto())
    }
    delete<EventResourcesV1.ByIdSeriesId> { route ->
        logger.info("📩 Received DELETE on /v1/event/${route.eventId}/series/${route.seriesId}")
        seriesService.removeSeries(route.seriesId.toSeriesId())
        val event = eventService.getEventWithSeries(route.eventId.toEventId())
        call.respond(event.toDto())
    }
    delete<EventResourcesV1.ByIdTeamsId> { route ->
        logger.info("📩 Received DELETE on /v1/event/${route.eventId}/teams/${route.teamId}")
        val event = eventService.removeTeam(route.eventId.toEventId(), route.teamId.toTeamId())
        call.respond(event.toDto())
    }
}
