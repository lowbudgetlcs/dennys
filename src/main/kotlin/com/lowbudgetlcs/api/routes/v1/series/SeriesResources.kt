package com.lowbudgetlcs.api.routes.v1.series

import io.ktor.resources.Resource

@Resource("/")
class SeriesResources {
    @Resource("{seriesId}")
    data class ById(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )

    @Resource("{seriesId}/game")
    data class ByIdGame(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )

    @Resource("{seriesId}/game/{gameId}")
    data class ByIdGameById(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
        val gameId: Int,
    )
}
