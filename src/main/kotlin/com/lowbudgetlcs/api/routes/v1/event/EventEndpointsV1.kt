package com.lowbudgetlcs.api.routes.v1.event

import com.lowbudgetlcs.api.dto.events.CreateEventDto
import com.lowbudgetlcs.api.dto.events.EventTeamLinkDto
import com.lowbudgetlcs.api.dto.events.PatchEventDto
import com.lowbudgetlcs.api.dto.events.toDto
import com.lowbudgetlcs.api.dto.events.toEventUpdate
import com.lowbudgetlcs.api.dto.events.toNewEvent
import com.lowbudgetlcs.api.dto.events.toTeamId
import com.lowbudgetlcs.api.dto.series.NewSeriesDto
import com.lowbudgetlcs.api.dto.series.toDto
import com.lowbudgetlcs.api.dto.series.toNewSeries
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.events.toStage
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.toSeriesId
import com.lowbudgetlcs.domain.services.event.IEventService
import com.lowbudgetlcs.domain.services.series.ISeriesService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventEndpointsV1(
    eventService: IEventService,
    seriesService: ISeriesService,
) {
    get<EventResourcesV1> {
        call.setCidContext {
            logCall(call)
            val events = eventService.getAllEvents()
            call.respond(events.map { it.toDto() })
        }
    }
    post<EventResourcesV1> {
        call.setCidContext {
            logCall(call)
            val dto = call.receive<CreateEventDto>()
            logger.debug(dto.toString())
            val created = eventService.createEvent(dto.toNewEvent())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
    get<EventResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val event = eventService.getEvent(route.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
    patch<EventResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<PatchEventDto>()
            logger.debug("\uD83C\uDF81 Body: {}", dto)
            val updated = eventService.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
            call.respond(updated.toDto())
        }
    }
    get<EventResourcesV1.ByIdTeams> { route ->
        call.setCidContext {
            logCall(call)
            val events = eventService.getEventWithTeams(route.eventId.toEventId())
            call.respond(events.toDto())
        }
    }
    post<EventResourcesV1.ByIdTeams> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<EventTeamLinkDto>()
            logger.debug(dto.toString())
            val event = eventService.addTeam(route.eventId.toEventId(), dto.toTeamId())
            call.respond(event.toDto())
        }
    }
    get<EventResourcesV1.ByIdSeries> { route ->
        call.setCidContext {
            logCall(call)
            // TODO: Use series service filter?
            val event = eventService.getEventWithSeries(route.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
    get<EventResourcesV1.ByIdFindSeries> { route ->
        call.setCidContext {
            logCall(call)
            val teamIds = route.teamIds.map { it.toTeamId() }
            if (teamIds.size != 2) throw IllegalArgumentException("Team IDs must have exactly 2 teams.")
            val stage = route.stage.toStage()
            val series = seriesService.findSeries(route.eventId.toEventId(), teamIds[0], teamIds[1], stage)
            call.respond(series.toDto())
        }
    }
    post<EventResourcesV1.ByIdSeries> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<NewSeriesDto>()
            logger.debug(dto.toString())
            val series = seriesService.createSeries(dto.toNewSeries(route.eventId))
            call.respond(HttpStatusCode.Created, series.toDto())
        }
    }
    delete<EventResourcesV1.ByIdSeriesId> { route ->
        call.setCidContext {
            logCall(call)
            seriesService.removeSeries(route.seriesId.toSeriesId())
            val event = eventService.getEventWithSeries(route.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
    delete<EventResourcesV1.ByIdTeamsId> { route ->
        call.setCidContext {
            logCall(call)
            val event = eventService.removeTeam(route.eventId.toEventId(), route.teamId.toTeamId())
            call.respond(event.toDto())
        }
    }
}
