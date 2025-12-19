package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.dto.riot.PostMatchDto
import com.lowbudgetlcs.api.routes.v1.account.accountRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.eventRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.group.eventGroupRoutesV1
import com.lowbudgetlcs.api.routes.v1.player.playerRoutesV1
import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.api.routes.v1.team.teamRoutesV1
import com.lowbudgetlcs.domain.services.account.IAccountService
import com.lowbudgetlcs.domain.services.event.IEventService
import com.lowbudgetlcs.domain.services.event.group.IEventGroupService
import com.lowbudgetlcs.domain.services.game.IGameService
import com.lowbudgetlcs.domain.services.player.IPlayerService
import com.lowbudgetlcs.domain.services.series.ISeriesService
import com.lowbudgetlcs.domain.services.team.ITeamService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.apiRoutes() {
    val eventService by inject<IEventService>()
    val teamService by inject<ITeamService>()
    val playerService by inject<IPlayerService>()
    val accountService by inject<IAccountService>()
    val seriesService by inject<ISeriesService>()
    val gameService by inject<IGameService>()
    val eventGroupService by inject<IEventGroupService>()

    route("/api/v1") {
        route("/riot-callback") {
            post {
                val callback = call.receive<PostMatchDto>()
                logger.info("📩 Received Riot callback: ${Json.encodeToString(PostMatchDto.serializer(), callback)}")
                call.respond(HttpStatusCode.OK)
                logger.info("✅ Callback successfully parsed!")
            }
        }
        authenticate("auth-session") {
            eventRoutesV1(eventService = eventService, seriesService = seriesService)
            teamRoutesV1(teamService = teamService)
            playerRoutesV1(playerService = playerService)
            accountRoutesV1(accountService = accountService)
            seriesRoutesV1(
                gameService = gameService,
            )
            eventGroupRoutesV1(eventGroupService)
        }
    }
}
