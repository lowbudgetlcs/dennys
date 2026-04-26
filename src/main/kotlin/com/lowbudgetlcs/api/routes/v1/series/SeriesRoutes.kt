package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.api.routes.v1.series.dto.SeriesResultDto
import com.lowbudgetlcs.api.routes.v1.series.dto.toDto
import com.lowbudgetlcs.api.routes.v1.series.dto.toSeriesResult
import com.lowbudgetlcs.api.routes.v1.series.game.dto.CreateGameDto
import com.lowbudgetlcs.api.routes.v1.series.game.dto.toDto
import com.lowbudgetlcs.api.routes.v1.series.game.dto.toNewGame
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.game.IGameService
import com.lowbudgetlcs.domain.series.models.toSeriesId
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

fun Route.seriesRoutesV1() {
    val seriesService by inject<ISeriesService>()
    val gameService by inject<IGameService>()
    route("/series") {
        get<SeriesResources.ById> { route ->
            val series = seriesService.getSeries(route.seriesId.toSeriesId())
            call.respond(series.toDto())
        }
        post<SeriesResources.ById> { route ->
            val dto = call.receive<SeriesResultDto>()
            logger.debug(dto.toString())
            val series = seriesService.completeSeries(dto.toSeriesResult(route.seriesId))
            call.respond(HttpStatusCode.OK, series.toDto())
        }
        // TODO: Move to game API route
        post<SeriesResources.ByIdGame> { route ->
            val dto = call.receive<CreateGameDto>()
            logger.debug(dto.toString())
            val created = gameService.createGame(dto.toNewGame(route.seriesId))
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
}
