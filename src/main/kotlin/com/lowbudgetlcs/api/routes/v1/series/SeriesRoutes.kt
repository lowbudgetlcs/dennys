package com.lowbudgetlcs.api.routes.v1.series

import com.lowbudgetlcs.api.dto.games.CreateGameDto
import com.lowbudgetlcs.api.dto.games.ReportResultDto
import com.lowbudgetlcs.api.dto.games.toDto
import com.lowbudgetlcs.api.dto.games.toNewTournamentCode
import com.lowbudgetlcs.api.dto.games.toReportedResult
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.models.toSeriesId
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
        post<SeriesResources.TournamentCode> { route ->
            val dto = call.receive<CreateGameDto>()
            logger.debug(dto.toString())
            val created = seriesService.createGame(dto.toNewTournamentCode(route.seriesId))
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        post<SeriesResources.Results> { route ->
            val dto = call.receive<ReportResultDto>()
            logger.debug(dto.toString())
            val outcome = seriesService.reportResult(route.seriesId.toSeriesId(), dto.toReportedResult())
            call.respond(
                if (outcome.recorded) HttpStatusCode.Created else HttpStatusCode.OK,
                outcome.game.toDto(),
            )
        }
    }
}
