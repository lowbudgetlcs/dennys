package com.lowbudgetlcs.api.routes.v1.stats

import com.lowbudgetlcs.domain.event.models.types.EventStage
import io.ktor.resources.Resource

@Resource("/")
class StatsResources {
    @Resource("player-games")
    data class PlayerGames(
        val parent: StatsResources = StatsResources(),
        val eventGroupId: Int? = null,
        val eventId: Int? = null,
        val stage: EventStage? = null,
        val teamId: Int? = null,
        val playerId: Int? = null,
        val championId: Int? = null,
        val minDurationSeconds: Int? = null,
        val limit: Int = 100,
        val offset: Int = 0,
    )

    @Resource("team-games")
    data class TeamGames(
        val parent: StatsResources = StatsResources(),
        val eventGroupId: Int? = null,
        val eventId: Int? = null,
        val stage: EventStage? = null,
        val teamId: Int? = null,
        val playerId: Int? = null,
        val minDurationSeconds: Int? = null,
        val limit: Int = 100,
        val offset: Int = 0,
    )

    @Resource("players")
    data class Players(
        val parent: StatsResources = StatsResources(),
        val eventGroupId: Int? = null,
        val eventId: Int? = null,
        val stage: EventStage? = null,
        val teamId: Int? = null,
        val playerId: Int? = null,
        val championId: Int? = null,
        val minDurationSeconds: Int? = null,
        val sort: String? = null,
        val minGames: Int? = null,
        val limit: Int = 100,
        val offset: Int = 0,
    )

    @Resource("teams")
    data class Teams(
        val parent: StatsResources = StatsResources(),
        val eventGroupId: Int? = null,
        val eventId: Int? = null,
        val stage: EventStage? = null,
        val teamId: Int? = null,
        val playerId: Int? = null,
        val minDurationSeconds: Int? = null,
        val sort: String? = null,
        val minGames: Int? = null,
        val limit: Int = 100,
        val offset: Int = 0,
    )

    @Resource("records")
    data class Records(
        val parent: StatsResources = StatsResources(),
        val eventGroupId: Int? = null,
        val eventId: Int? = null,
        val stage: EventStage? = null,
        val teamId: Int? = null,
        val limit: Int = 100,
        val offset: Int = 0,
    )

    @Resource("champions")
    data class Champions(
        val parent: StatsResources = StatsResources(),
        val eventGroupId: Int? = null,
        val eventId: Int? = null,
        val stage: EventStage? = null,
        val teamId: Int? = null,
        val playerId: Int? = null,
        val championId: Int? = null,
        val minDurationSeconds: Int? = null,
        val sort: String? = null,
        val minPicks: Int? = null,
        val limit: Int = 100,
        val offset: Int = 0,
    )
}
