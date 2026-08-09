package com.lowbudgetlcs.api.routes.v1.event

import com.lowbudgetlcs.api.dto.events.CreateEventDto
import com.lowbudgetlcs.api.dto.events.EventFilterParams
import com.lowbudgetlcs.api.dto.events.EventTeamLinkDto
import com.lowbudgetlcs.api.dto.events.PatchEventDto
import com.lowbudgetlcs.api.dto.events.toDto
import com.lowbudgetlcs.api.dto.events.toEventUpdate
import com.lowbudgetlcs.api.dto.events.toNewEvent
import com.lowbudgetlcs.api.dto.events.toQuery
import com.lowbudgetlcs.api.dto.events.toTeamId
import com.lowbudgetlcs.api.dto.series.NewSeriesDto
import com.lowbudgetlcs.api.dto.series.SeriesFilterParams
import com.lowbudgetlcs.api.dto.series.toDto
import com.lowbudgetlcs.api.dto.series.toNewSeries
import com.lowbudgetlcs.api.dto.series.toQuery
import com.lowbudgetlcs.domain.event.IEventService
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventRoutesV1(
    eventService: IEventService,
    seriesService: ISeriesService,
) {
    route("/event") {
        get<EventResources> { route ->
            val filter =
                EventFilterParams(
                    name = route.name,
                    status = route.status,
                )
            val events = eventService.getAllEvents(filter.toQuery())
            call.respond(events.map { it.toDto() })
        }
        post<EventResources> {
            val dto = call.receive<CreateEventDto>()
            logger.debug(dto.toString())
            val created = eventService.createEvent(dto.toNewEvent())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<EventResources.ById> { route ->
            val event = eventService.getEvent(route.eventId.toEventId())
            call.respond(event.toDto())
        }
        patch<EventResources.ById> { route ->
            val dto = call.receive<PatchEventDto>()
            logger.debug("\uD83C\uDF81 Body: {}", dto)
            val updated = eventService.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
            call.respond(updated.toDto())
        }
        get<EventResources.ByIdTeams> { route ->
            val events = eventService.getEventWithTeams(route.eventId.toEventId())
            call.respond(events.toDto())
        }
        post<EventResources.ByIdTeams> { route ->
            val dto = call.receive<EventTeamLinkDto>()
            logger.debug(dto.toString())
            val event = eventService.addTeam(route.eventId.toEventId(), dto.toTeamId())
            call.respond(event.toDto())
        }
        get<EventResources.ByIdSeries> { route ->
            val filter =
                SeriesFilterParams(
                    teamIds = route.teamIds,
                    stage = route.stage,
                    completed = route.completed,
                )
            val event = eventService.getEventWithSeries(route.eventId.toEventId(), filter.toQuery())

            call.respond(event.toDto())
        }
        post<EventResources.ByIdSeries> { route ->
            val dto = call.receive<NewSeriesDto>()
            logger.debug(dto.toString())
            val series = seriesService.createSeries(dto.toNewSeries(route.eventId))
            call.respond(HttpStatusCode.Created, series.toDto())
        }
        delete<EventResources.ByIdSeriesId> { route ->
            seriesService.removeSeries(route.seriesId.toSeriesId())
            val event = eventService.getEventWithSeries(route.eventId.toEventId())
            call.respond(event.toDto())
        }
        delete<EventResources.ByIdTeamsId> { route ->
            val event = eventService.removeTeam(route.eventId.toEventId(), route.teamId.toTeamId())
            call.respond(event.toDto())
        }
    }
}
