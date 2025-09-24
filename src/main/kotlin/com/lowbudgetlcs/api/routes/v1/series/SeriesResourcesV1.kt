package com.lowbudgetlcs.api.routes.v1.series

import io.ktor.resources.*

@Resource("/")
class SeriesResourcesV1 {
    @Resource("game")
    data class Game(
        val parent: SeriesResourcesV1 = SeriesResourcesV1(),
    )
}
