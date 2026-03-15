package com.lowbudgetlcs.api.routes.v1.event

import com.lowbudgetlcs.api.dto.events.*
import com.lowbudgetlcs.api.dto.series.*
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.config.StorageConfig
import com.lowbudgetlcs.domain.event.IEventService
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventRoutesV1(
    eventService: IEventService,
    seriesService: ISeriesService,
    storageConfig: StorageConfig,
) {
    route("/event") {
        get<EventResources> { route ->
            call.setCidContext {
                logCall(call)
                val filter =
                    EventFilterParams(
                        name = route.name,
                        status = route.status,
                    )
                val events = eventService.getAllEvents(filter.toQuery())
                call.respond(events.map { it.toDto() })
            }
        }
        post<EventResources> {
            call.setCidContext {
                logCall(call)
                val dto = call.receive<CreateEventDto>()
                logger.debug(dto.toString())
                val created = eventService.createEvent(dto.toNewEvent())
                call.respond(HttpStatusCode.Created, created.toDto())
            }
        }
        get<EventResources.ById> { route ->
            call.setCidContext {
                logCall(call)
                val event = eventService.getEvent(route.eventId.toEventId())
                call.respond(event.toDto())
            }
        }
        patch<EventResources.ById> { route ->
            call.setCidContext {
                logCall(call)
                val dto = call.receive<PatchEventDto>()
                logger.debug("\uD83C\uDF81 Body: {}", dto)
                val updated = eventService.patchEvent(route.eventId.toEventId(), dto.toEventUpdate())
                call.respond(updated.toDto())
            }
        }
        get<EventResources.ByIdTeams> { route ->
            call.setCidContext {
                logCall(call)
                val events = eventService.getEventWithTeams(route.eventId.toEventId())
                call.respond(events.toDto(storageConfig.logobucketurl))
            }
        }
        post<EventResources.ByIdTeams> { route ->
            call.setCidContext {
                logCall(call)
                val dto = call.receive<EventTeamLinkDto>()
                logger.debug(dto.toString())
                val event = eventService.addTeam(route.eventId.toEventId(), dto.toTeamId())
                call.respond(event.toDto(storageConfig.logobucketurl))
            }
        }
        get<EventResources.ByIdSeries> { route ->
            call.setCidContext {
                logCall(call)
                val filter =
                    SeriesFilterParams(
                        teamIds = route.teamIds,
                        stage = route.stage,
                    )
                val event = eventService.getEventWithSeries(route.eventId.toEventId(), filter.toQuery())

                call.respond(event.toDto())
            }
        }
        post<EventResources.ByIdSeries> { route ->
            call.setCidContext {
                logCall(call)
                val dto = call.receive<NewSeriesDto>()
                logger.debug(dto.toString())
                val series = seriesService.createSeries(dto.toNewSeries(route.eventId))
                call.respond(HttpStatusCode.Created, series.toDto())
            }
        }
        delete<EventResources.ByIdSeriesId> { route ->
            call.setCidContext {
                logCall(call)
                seriesService.removeSeries(route.seriesId.toSeriesId())
                val event = eventService.getEventWithSeries(route.eventId.toEventId())
                call.respond(event.toDto())
            }
        }
        delete<EventResources.ByIdTeamsId> { route ->
            call.setCidContext {
                logCall(call)
                val event = eventService.removeTeam(route.eventId.toEventId(), route.teamId.toTeamId())
                call.respond(event.toDto(storageConfig.logobucketurl))
            }
        }
    }
}
