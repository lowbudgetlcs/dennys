package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.routes.v1.event.group.eventGroupRoutesV1
import com.lowbudgetlcs.api.routes.v1.player.playerRoutesV1
import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.api.routes.v1.team.teamRoutesV1
import com.lowbudgetlcs.domain.account.IAccountService
import com.lowbudgetlcs.domain.account.routes.accountRoutesV1
import com.lowbudgetlcs.domain.event.core.IEventService
import com.lowbudgetlcs.domain.event.adapter.`in`.web.eventRoutesV1
import com.lowbudgetlcs.domain.eventgroup.IEventGroupService
import com.lowbudgetlcs.domain.player.IPlayerService
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.team.ITeamService
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
