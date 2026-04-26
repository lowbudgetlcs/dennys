package com.lowbudgetlcs.api.routes.v1.series.game

import io.ktor.resources.Resource

@Resource("/")
class GameResources {
    @Resource("{gameId}")
    data class ById(
        val parent: GameResources = GameResources(),
        val gameId: Int,
    )
}
