package com.lowbudgetlcs.domain.division.adapter.`in`.web

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.CreateGameDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.division.adapter.`in`.web.dto.toNewGame
import com.lowbudgetlcs.domain.division.core.series.services.SeriesService
import com.lowbudgetlcs.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route


class SeriesRoutesV1(private val seriesService: SeriesService) : ApiRoute {
    override fun register(routing: Route) {
        routing.route("/series") {
            post<SeriesResources.Game> { route ->
                val dto = call.receive<CreateGameDto>()
                logger.debug(dto.toString())
                val created = seriesService.createGame(dto.toNewGame(route.seriesId))
                call.respond(HttpStatusCode.Created, created.toDto())
            }
        }
    }
}
