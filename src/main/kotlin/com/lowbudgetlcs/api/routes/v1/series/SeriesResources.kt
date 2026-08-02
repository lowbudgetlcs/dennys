package com.lowbudgetlcs.api.routes.v1.series

import io.ktor.resources.Resource

@Resource("/")
class SeriesResources {
    @Resource("{seriesId}/game")
    data class TournamentCode(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )
}
