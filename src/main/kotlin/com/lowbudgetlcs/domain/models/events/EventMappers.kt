package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.team.Team

fun Event.toEventWithTeams(teams: List<Team>): EventWithTeams =
    EventWithTeams(
        id = id,
        name = name,
        description = description,
        eventGroupId = eventGroupId,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        status = status,
        teams = teams,
        stages = stages,
    )

fun Event.toEventWithSeries(series: List<Series>): EventWithSeries =
    EventWithSeries(
        id = id,
        name = name,
        description = description,
        eventGroupId = eventGroupId,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        status = status,
        series = series,
        stages = stages,
    )

fun String.toStage(): Stage =
    try {
        enumValueOf<Stage>(this)
    } catch (_: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid stage.")
    }
