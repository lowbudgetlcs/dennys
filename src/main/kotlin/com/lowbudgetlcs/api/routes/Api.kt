package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.domain.event.adapter.`in`.web.eventGroupRoutesV1
import com.lowbudgetlcs.domain.player.adapter.`in`.web.playerRoutesV1
import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.domain.team.adapter.`in`.web.teamRoutesV1
import com.lowbudgetlcs.domain.account.core.port.IAccountService
import com.lowbudgetlcs.domain.account.adapter.`in`.web.accountRoutesV1
import com.lowbudgetlcs.domain.event.core.port.IEventService
import com.lowbudgetlcs.domain.event.adapter.`in`.web.eventRoutesV1
import com.lowbudgetlcs.domain.event.core.port.IEventGroupService
import com.lowbudgetlcs.domain.player.core.port.IPlayerService
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.team.core.port.ITeamService
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.apiRoutes() {
    val eventService by inject<IEventService>()
    val teamService by inject<ITeamService>()
    val playerService by inject<IPlayerService>()
    val accountService by inject<IAccountService>()
    val seriesService by inject<ISeriesService>()
    val eventGroupService by inject<IEventGroupService>()

    route("/api/v1") {
        authenticate("auth-session", "auth-token") {
            eventRoutesV1(eventService = eventService, seriesService = seriesService)
            teamRoutesV1(teamService = teamService)
            playerRoutesV1(playerService = playerService)
            accountRoutesV1(accountService = accountService)
            seriesRoutesV1(
                seriesService = seriesService,
            )
            eventGroupRoutesV1(eventGroupService)
        }
    }
}
