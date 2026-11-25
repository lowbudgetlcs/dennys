package com.lowbudgetlcs.api.routes.v1.stats

import com.lowbudgetlcs.api.dto.stats.toDto
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupId
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.stats.IStatsService
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.statsRoutesV1(statsService: IStatsService) {
    route("/stats") {
        get<StatsResources.PlayerGames> { r ->
            val query =
                statsQuery(r.eventGroupId, r.eventId, r.stage, r.teamId, r.playerId, r.championId, r.minDurationSeconds)
            call.respond(statsService.getPlayerGames(query, r.limit, r.offset).map { it.toDto() })
        }
        get<StatsResources.TeamGames> { r ->
            val query = statsQuery(r.eventGroupId, r.eventId, r.stage, r.teamId, r.playerId, null, r.minDurationSeconds)
            call.respond(statsService.getTeamGames(query, r.limit, r.offset).map { it.toDto() })
        }
        get<StatsResources.Players> { r ->
            val query =
                statsQuery(r.eventGroupId, r.eventId, r.stage, r.teamId, r.playerId, r.championId, r.minDurationSeconds)
            val sort = r.sort?.let(StatsSort::parse)
            call.respond(
                statsService.getPlayerAggregates(query, sort, r.limit, r.offset, r.minGames).map { it.toDto() },
            )
        }
        get<StatsResources.Teams> { r ->
            val query = statsQuery(r.eventGroupId, r.eventId, r.stage, r.teamId, r.playerId, null, r.minDurationSeconds)
            val sort = r.sort?.let(StatsSort::parse)
            call.respond(statsService.getTeamAggregates(query, sort, r.limit, r.offset, r.minGames).map { it.toDto() })
        }
        get<StatsResources.Champions> { r ->
            val query =
                statsQuery(r.eventGroupId, r.eventId, r.stage, r.teamId, r.playerId, r.championId, r.minDurationSeconds)
            val sort = r.sort?.let(StatsSort::parse)
            call.respond(
                statsService.getChampionAggregates(query, sort, r.limit, r.offset, r.minPicks).map { it.toDto() },
            )
        }
    }
}

@Suppress("LongParameterList")
private fun statsQuery(
    eventGroupId: Int?,
    eventId: Int?,
    stage: EventStage?,
    teamId: Int?,
    playerId: Int?,
    championId: Int?,
    minDurationSeconds: Int?,
) = StatsQuery(
    eventGroupId = eventGroupId?.toEventGroupId(),
    eventId = eventId?.toEventId(),
    stage = stage,
    teamId = teamId?.toTeamId(),
    playerId = playerId?.toPlayerId(),
    championId = championId,
    minDurationSeconds = minDurationSeconds,
)
