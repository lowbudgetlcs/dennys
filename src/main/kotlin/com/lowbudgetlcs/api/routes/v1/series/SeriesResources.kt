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
    data class TournamentCode(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )

    @Resource("{seriesId}/results")
    data class Results(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )

    @Resource("{seriesId}/complete")
    data class Complete(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )
}
