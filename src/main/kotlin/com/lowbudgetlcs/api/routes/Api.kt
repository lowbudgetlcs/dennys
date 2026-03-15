package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.dto.riot.PostMatchDto
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.routes.v1.account.accountRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.eventRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.group.eventGroupRoutesV1
import com.lowbudgetlcs.api.routes.v1.player.playerRoutesV1
import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.api.routes.v1.team.teamRoutesV1
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.config.StorageConfig
import com.lowbudgetlcs.domain.account.IAccountService
import com.lowbudgetlcs.domain.event.IEventService
import com.lowbudgetlcs.domain.eventgroup.IEventGroupService
import com.lowbudgetlcs.domain.player.IPlayerService
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.team.ITeamService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
    val eventGroupService by inject<IEventGroupService>()
    val storageConfig by inject<StorageConfig>()

    route("/api/v1") {
        route("/riot-callback") {
            post {
                call.setCidContext {
                    logCall(call)
                    val dto = call.receive<PostMatchDto>()
                    logger.debug(dto.toString())
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        authenticate("auth-session", "auth-token") {
            eventRoutesV1(eventService = eventService, seriesService = seriesService, storageConfig = storageConfig)
            teamRoutesV1(teamService = teamService, storageConfig = storageConfig)
            playerRoutesV1(playerService = playerService)
            accountRoutesV1(accountService = accountService)
            seriesRoutesV1(
                seriesService = seriesService,
            )
            eventGroupRoutesV1(eventGroupService)
        }
    }
}
