package com.lowbudgetlcs.domain.series.adapter.`in`.web

import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.CreateGameDto
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.series.adapter.`in`.web.dto.toNewGame
import com.lowbudgetlcs.domain.series.core.port.ISeriesService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.seriesRoutesV1(seriesService: ISeriesService) {
    route("/series") {
        post<SeriesResources.Game> { route ->
            val dto = call.receive<CreateGameDto>()
            logger.debug(dto.toString())
            val created = seriesService.createGame(dto.toNewGame(route.seriesId))
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
}
