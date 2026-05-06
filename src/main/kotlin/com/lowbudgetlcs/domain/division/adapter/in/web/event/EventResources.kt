package com.lowbudgetlcs.domain.division.adapter.`in`.web.event

import io.ktor.resources.Resource

class EventResources {
    @Resource("/event")
    data class Base(
        val name: String? = null,
        val status: String? = null,
    )

    @Resource("/event/{eventId}")
    data class Id(
        val eventId: Int,
    )

    @Resource("/event/{eventId}/teams")
    data class IdWithTeams(
        val eventId: Int,
    )

    @Resource("/event/{eventId}/teams/{teamId}")
    data class IdWithTeamsId(
        val eventId: Int,
        val teamId: Int,
    )

    @Resource("/event/{eventId}/series")
    data class IdWithSeries(
        val eventId: Int,
        val teamIds: List<Int>? = null,
        val stage: String? = null,
    )

    @Resource("/event/{eventId}/series/{seriesId}")
    data class IdWithSeriesId(
        val eventId: Int,
        val seriesId: Int,
    )
}
