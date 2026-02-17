package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.api.dto.games.CreateGameDto
import com.lowbudgetlcs.api.dto.games.toDto
import com.lowbudgetlcs.api.dto.games.toNewGame
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.services.series.ISeriesService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.seriesEndpointsV1(seriesService: ISeriesService) {
    post<SeriesResourcesV1.Game> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<CreateGameDto>()
            logger.debug(dto.toString())
            val created = seriesService.createGame(dto.toNewGame(route.seriesId))
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
}
