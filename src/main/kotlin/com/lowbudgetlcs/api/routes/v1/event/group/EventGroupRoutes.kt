package com.lowbudgetlcs.api.routes.v1.event.group

import com.lowbudgetlcs.api.routes.v1.event.group.dto.CreateEventGroupDto
import com.lowbudgetlcs.api.routes.v1.event.group.dto.EventGroupAddEventDto
import com.lowbudgetlcs.api.routes.v1.event.group.dto.PatchEventGroupDto
import com.lowbudgetlcs.api.routes.v1.event.group.dto.toDto
import com.lowbudgetlcs.api.routes.v1.event.group.dto.toEventGroupUpdate
import com.lowbudgetlcs.api.routes.v1.event.group.dto.toNewEventGroup
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.eventgroup.IEventGroupService
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupId
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
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.eventGroupRoutesV1() {
    val eventGroupService by inject<IEventGroupService>()
    route("/eventGroup") {
        get<EventGroupResources> {
            val groups = eventGroupService.getAllEventGroups()
            call.respond(groups.map { it.toDto() })
        }
        post<EventGroupResources> {
            val dto = call.receive<CreateEventGroupDto>()
            logger.debug(dto.toString())
            val created = eventGroupService.createEventGroup(dto.toNewEventGroup())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<EventGroupResources.ById> { route ->
            val event = eventGroupService.getEventGroup(route.eventGroupId.toEventGroupId())
            call.respond(event.toDto())
        }
        patch<EventGroupResources.ById> { route ->
            val dto = call.receive<PatchEventGroupDto>()
            logger.debug("\uD83C\uDF81 Body: {}", dto)
            val updated =
                eventGroupService.patchEventGroup(route.eventGroupId.toEventGroupId(), dto.toEventGroupUpdate())
            call.respond(updated.toDto())
        }
        get<EventGroupResources.ByIdEvents> { route ->
            val groups = eventGroupService.getEventGroupWithEvents(route.eventGroupId.toEventGroupId())
            call.respond(groups.toDto())
        }
        post<EventGroupResources.ByIdEvents> { route ->
            val dto = call.receive<EventGroupAddEventDto>()
            logger.debug(dto.toString())
            val event = eventGroupService.addEvent(route.eventGroupId.toEventGroupId(), dto.eventId.toEventId())
            call.respond(event.toDto())
        }
        delete<EventGroupResources.ByIdEventId> { route ->
            val event =
                eventGroupService.removeEvent(route.eventGroupId.toEventGroupId(), route.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
}
