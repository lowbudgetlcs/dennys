package com.lowbudgetlcs.domain.series.adapter.`in`.web

import io.ktor.resources.Resource

@Resource("/")
class SeriesResources {
    @Resource("{seriesId}/game")
    data class Game(
        val parent: SeriesResources = SeriesResources(),
        val seriesId: Int,
    )
}
