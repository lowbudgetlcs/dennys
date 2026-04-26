package com.lowbudgetlcs.api.routes.v1.series.game

import com.lowbudgetlcs.api.routes.v1.series.game.dto.GameResultDto
import com.lowbudgetlcs.api.routes.v1.series.game.dto.toDto
import com.lowbudgetlcs.api.routes.v1.series.game.dto.toGameResult
import com.lowbudgetlcs.domain.series.game.IGameService
import com.lowbudgetlcs.domain.series.game.models.toGameId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.gameRoutesV1() {
    val gameService by inject<IGameService>()
    route("/game") {
        get<GameResources.ById> { route ->
            val game = gameService.getById(route.gameId.toGameId())
            call.respond(HttpStatusCode.OK, game.toDto())
        }
        post<GameResources.ById> { route ->
            val dto = call.receive<GameResultDto>()
            logger.debug(dto.toString())
            val game = gameService.completeGame(dto.toGameResult(gameId = route.gameId))
            call.respond(HttpStatusCode.OK, game.toDto())
        }
    }
}
