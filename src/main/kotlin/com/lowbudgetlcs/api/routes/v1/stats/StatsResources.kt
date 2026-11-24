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
        val limit: Int = 100,
        val offset: Int = 0,
    )
}
