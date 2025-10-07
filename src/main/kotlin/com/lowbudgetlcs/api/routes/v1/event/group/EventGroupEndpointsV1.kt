package com.lowbudgetlcs.api.routes.v1.event.group

import com.lowbudgetlcs.api.dto.events.groups.CreateEventGroupDto
import com.lowbudgetlcs.api.dto.events.groups.EventGroupAddEventDto
import com.lowbudgetlcs.api.dto.events.groups.PatchEventGroupDto
import com.lowbudgetlcs.api.dto.events.groups.toDto
import com.lowbudgetlcs.api.dto.events.groups.toEventGroupUpdate
import com.lowbudgetlcs.api.dto.events.groups.toNewEventGroup
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.models.events.group.toEventGroupId
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.services.event.group.IEventGroupService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun logCall(call: RoutingCall) {
    logger.info("📩 Received ${call.request.httpMethod} on ${call.request.path()}")
}

fun Route.eventGroupEndpointsV1(eventGroupService: IEventGroupService) {
    get<EventGroupResourcesV1> {
        call.setCidContext {
            logCall(call)
            val groups = eventGroupService.getAllEventGroups()
            call.respond(groups.map { it.toDto() })
        }
    }
    post<EventGroupResourcesV1> {
        call.setCidContext {
            logCall(call)
            val dto = call.receive<CreateEventGroupDto>()
            logger.debug(dto.toString())
            val created = eventGroupService.createEventGroup(dto.toNewEventGroup())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
    get<EventGroupResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val event = eventGroupService.getEventGroup(route.eventGroupId.toEventGroupId())
            call.respond(event.toDto())
        }
    }
    patch<EventGroupResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<PatchEventGroupDto>()
            logger.debug("\uD83C\uDF81 Body: {}", dto)
            val updated =
                eventGroupService.patchEventGroup(route.eventGroupId.toEventGroupId(), dto.toEventGroupUpdate())
            call.respond(updated.toDto())
        }
    }
    get<EventGroupResourcesV1.ByIdEvents> { route ->
        call.setCidContext {
            logCall(call)
            val groups = eventGroupService.getEventGroupWithEvents(route.eventGroupId.toEventGroupId())
            call.respond(groups.toDto())
        }
    }
    post<EventGroupResourcesV1.ByIdEvents> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<EventGroupAddEventDto>()
            logger.debug(dto.toString())
            val event = eventGroupService.addEvent(route.eventGroupId.toEventGroupId(), dto.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
    delete<EventGroupResourcesV1.ByIdEventId> { route ->
        call.setCidContext {
            logCall(call)
            val event = eventGroupService.removeEvent(route.eventGroupId.toEventGroupId(), route.eventId.toEventId())
            call.respond(event.toDto())
        }
    }
}
