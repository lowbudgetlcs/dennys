package com.lowbudgetlcs.api.routes.v1.game

import io.ktor.resources.Resource

@Resource("/")
class GameResources {
    @Resource("{gameId}/match")
    data class Match(
        val parent: GameResources = GameResources(),
        val gameId: Int,
    )

    @Resource("{gameId}/timeline")
    data class Timeline(
        val parent: GameResources = GameResources(),
        val gameId: Int,
    )

    @Resource("{gameId}/stats")
    data class Stats(
        val parent: GameResources = GameResources(),
        val gameId: Int,
    )
}
