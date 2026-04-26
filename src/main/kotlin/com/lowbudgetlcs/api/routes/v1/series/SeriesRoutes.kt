package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.api.dto.games.CreateGameDto
import com.lowbudgetlcs.api.dto.games.toDto
import com.lowbudgetlcs.api.dto.games.toNewGame
import com.lowbudgetlcs.domain.series.game.IGameService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.seriesRoutesV1() {
    val gameService by inject<IGameService>()
    route("/series") {
        post<SeriesResources.Game> { route ->
            val dto = call.receive<CreateGameDto>()
            logger.debug(dto.toString())
            val created = gameService.createGame(dto.toNewGame(route.seriesId))
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
}
