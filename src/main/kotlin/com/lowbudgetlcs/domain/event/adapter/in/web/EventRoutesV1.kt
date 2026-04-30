package com.lowbudgetlcs.domain.event.adapter.`in`.web

import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.CreateEventDto
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.EventFilterParams
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.EventTeamLinkDto
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.PatchEventDto
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toEventStage
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toEventUpdate
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toNewEvent
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toQuery
import com.lowbudgetlcs.domain.event.adapter.`in`.web.dto.toTeamId
import com.lowbudgetlcs.domain.event.core.model.types.toEventId
import com.lowbudgetlcs.domain.event.core.port.IEventService
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.NewSeriesDto
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.SeriesFilterParams
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.toNewSeries
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.toQuery
import com.lowbudgetlcs.domain.series.core.model.types.toSeriesId
import com.lowbudgetlcs.domain.series.core.port.ISeriesService
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles


private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
fun Route.eventRoutesV1() {
    val eventService by inject<IEventService>()
    val seriesService by inject<ISeriesService>()

    route("/event") {
        get<EventResourcesV1> { route ->
            val filter =
                EventFilterParams(
                    name = route.name,
                    status = route.status,
                )
            val events = eventService.getAllEvents(filter.toQuery())
            call.respond(events.map { it.toDto() })
        }
        post<EventResourcesV1> {
            val dto = call.receive<CreateEventDto>()
            logger.debug(dto.toString())
            val created = eventService.createEvent(dto.toNewEvent())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<EventResourcesV1.ById> { route ->
            val event = eventService.getEvent(route.eventId.toEventId())
            call.respond(event.toDto())
        }
        patch<EventResourcesV1.ById> { route ->
            val dto = call.receive<PatchEventDto>()
            logger.debug("\uD83C\uDF81 Body: {}", dto)
            val updated = eventService.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
            call.respond(updated.toDto())
        }
        get<EventResourcesV1.ByIdTeams> { route ->
            val events = eventService.getEventWithTeams(route.eventId.toEventId())
            call.respond(events.toDto())
        }
        post<EventResourcesV1.ByIdTeams> { route ->
            val dto = call.receive<EventTeamLinkDto>()
            logger.debug(dto.toString())
            val event = eventService.addTeam(route.eventId.toEventId(), dto.toTeamId())
            call.respond(event.toDto())
        }
        get<EventResourcesV1.ByIdSeries> { route ->
            val filter =
                SeriesFilterParams(
                    teamIds = route.teamIds,
                    stage = route.stage?.toEventStage(),
                )
            val event = eventService.getEventWithSeries(route.eventId.toEventId(), filter.toQuery())

            call.respond(event.toDto())
        }
        post<EventResourcesV1.ByIdSeries> { route ->
            val dto = call.receive<NewSeriesDto>()
            logger.debug(dto.toString())
            val series = seriesService.createSeries(dto.toNewSeries(route.eventId))
            call.respond(HttpStatusCode.Created, series.toDto())
        }
        delete<EventResourcesV1.ByIdSeriesId> { route ->
            seriesService.removeSeries(route.seriesId.toSeriesId())
            val event = eventService.getEventWithSeries(route.eventId.toEventId())
            call.respond(event.toDto())
        }
        delete<EventResourcesV1.ByIdTeamsId> { route ->
            val event = eventService.removeTeam(route.eventId.toEventId(), route.teamId.toTeamId())
            call.respond(event.toDto())
        }
    }
}
