package com.lowbudgetlcs.api.routes.v1.stats

import com.lowbudgetlcs.api.dto.stats.toDto
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupId
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.stats.IStatsService
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.statsRoutesV1(statsService: IStatsService) {
    route("/stats") {
        get<StatsResources.PlayerGames> { route ->
            val query =
                StatsQuery(
                    eventGroupId = route.eventGroupId?.toEventGroupId(),
                    eventId = route.eventId?.toEventId(),
                    stage = route.stage,
                    teamId = route.teamId?.toTeamId(),
                    playerId = route.playerId?.toPlayerId(),
                    championId = route.championId,
                )
            call.respond(statsService.getPlayerGames(query, route.limit, route.offset).map { it.toDto() })
        }
        get<StatsResources.TeamGames> { route ->
            val query =
                StatsQuery(
                    eventGroupId = route.eventGroupId?.toEventGroupId(),
                    eventId = route.eventId?.toEventId(),
                    stage = route.stage,
                    teamId = route.teamId?.toTeamId(),
                    playerId = route.playerId?.toPlayerId(),
                )
            call.respond(statsService.getTeamGames(query, route.limit, route.offset).map { it.toDto() })
        }
    }
}
