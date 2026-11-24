package com.lowbudgetlcs.api.routes.v1.game

import com.lowbudgetlcs.api.dto.stats.toDto
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.stats.IStatsService
import io.ktor.http.HttpHeaders
import io.ktor.server.resources.get
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

// Riot never changes a stored match, but stats rows change when an account is linked or a result corrected.
private const val IMMUTABLE = "private, max-age=31536000, immutable"
private const val SHORT_LIVED = "private, max-age=300"

fun Route.gameRoutesV1(statsService: IStatsService) {
    route("/games") {
        get<GameResources.Match> { route ->
            val match = statsService.getMatch(route.gameId.toGameId())
            call.response.header(HttpHeaders.CacheControl, IMMUTABLE)
            call.respond(match)
        }
        get<GameResources.Timeline> { route ->
            val timeline = statsService.getTimeline(route.gameId.toGameId())
            call.response.header(HttpHeaders.CacheControl, IMMUTABLE)
            call.respond(timeline)
        }
        get<GameResources.Stats> { route ->
            val stats = statsService.getGameStats(route.gameId.toGameId())
            call.response.header(HttpHeaders.CacheControl, SHORT_LIVED)
            call.respond(stats.toDto())
        }
    }
}
