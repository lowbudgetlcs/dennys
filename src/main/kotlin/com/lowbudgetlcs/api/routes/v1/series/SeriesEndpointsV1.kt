package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.domain.services.game.IGameService
import com.lowbudgetlcs.api.dto.games.CreateGameDto
import com.lowbudgetlcs.api.dto.games.toDto
import com.lowbudgetlcs.api.dto.games.toNewGame
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)
fun Route.seriesEndpointsV1(gameService: IGameService) {
    post<SeriesResourcesV1.Game> {
        logger.info("📩 Received POST on /v1/series/game")
        val dto = call.receive<CreateGameDto>()
        logger.debug("\uD83C\uDF81 Body: {}", dto)
        val created = gameService.createGame(dto.toNewGame())
        call.respond(HttpStatusCode.Created, created.toDto())
    }
}