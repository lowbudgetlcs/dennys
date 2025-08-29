package com.lowbudgetlcs.api.routes.v1.event

import com.lowbudgetlcs.domain.services.IEventService
import com.lowbudgetlcs.domain.services.ISeriesService
import com.lowbudgetlcs.api.dto.events.CreateEventDto
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*

fun Route.eventRoutesV1(eventService: IEventService, seriesService: ISeriesService) {
    route("/event") {
        install(RequestValidation) {
            validate<CreateEventDto> { dto ->
                when {
                    dto.name.isBlank() -> ValidationResult.Invalid("Event name cannot be blank.")
                    dto.endDate.isBefore(dto.startDate) ->
                        ValidationResult.Invalid("Events cannot end before they start.")

                    else -> ValidationResult.Valid
                }
            }
        }
        eventEndpointsV1(eventService, seriesService)
    }
}
