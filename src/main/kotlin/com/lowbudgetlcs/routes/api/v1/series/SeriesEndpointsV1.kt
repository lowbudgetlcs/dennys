package com.lowbudgetlcs.routes.api.v1.series

import com.lowbudgetlcs.domain.services.IGameService
import com.lowbudgetlcs.routes.dto.games.CreateGameDto
import com.lowbudgetlcs.routes.dto.games.toDto
import com.lowbudgetlcs.routes.dto.games.toNewGame
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SeriesEndpointsV1")
fun Route.seriesEndpointsV1(gameService: IGameService) {
    post<SeriesResourcesV1.Game> {
        logger.info("📩 Received POST on /v1/series/game")
        val dto = call.receive<CreateGameDto>()
        logger.debug("Body: {}", dto)
        val created = gameService.createGame(dto.toNewGame())
        call.respond(HttpStatusCode.Created, created.toDto())
    }
}