package com.lowbudgetlcs.domain.division.adapter.`in`.web

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.CreateEventGroupDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.EventGroupAddEventDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.PatchEventGroupDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toEventGroupUpdate
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toNewEventGroup
import com.lowbudgetlcs.domain.division.core.services.EventGroupService
import com.lowbudgetlcs.domain.division.core.model.types.toEventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.toEventId
import com.lowbudgetlcs.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route


class EventGroupRoutesV1(private val eventGroupService: EventGroupService) : ApiRoute {
    override fun register(routing: Route) {
        routing.route("/eventGroup") {
            get<EventGroupResourcesV1> {
                val groups = eventGroupService.getAllEventGroups()
                call.respond(groups.map { it.toDto() })
            }
            post<EventGroupResourcesV1> {
                val dto = call.receive<CreateEventGroupDto>()
                logger.debug(dto.toString())
                val created = eventGroupService.createEventGroup(dto.toNewEventGroup())
                call.respond(HttpStatusCode.Created, created.toDto())
            }
            get<EventGroupResourcesV1.ById> { route ->
                val event = eventGroupService.getEventGroup(route.eventGroupId.toEventGroupId())
                call.respond(event.toDto())
            }
            patch<EventGroupResourcesV1.ById> { route ->
                val dto = call.receive<PatchEventGroupDto>()
                logger.debug("\uD83C\uDF81 Body: {}", dto)
                val updated =
                    eventGroupService.patchEventGroup(route.eventGroupId.toEventGroupId(), dto.toEventGroupUpdate())
                call.respond(updated.toDto())
            }
            get<EventGroupResourcesV1.ByIdEvents> { route ->
                val groups = eventGroupService.getEventGroupWithEvents(route.eventGroupId.toEventGroupId())
                call.respond(groups.toDto())
            }
            post<EventGroupResourcesV1.ByIdEvents> { route ->
                val dto = call.receive<EventGroupAddEventDto>()
                logger.debug(dto.toString())
                val event = eventGroupService.addEvent(route.eventGroupId.toEventGroupId(), dto.eventId.toEventId())
                call.respond(event.toDto())
            }
            delete<EventGroupResourcesV1.ByIdEventId> { route ->
                val event =
                    eventGroupService.removeEvent(route.eventGroupId.toEventGroupId(), route.eventId.toEventId())
                call.respond(event.toDto())
            }
        }
    }
}
