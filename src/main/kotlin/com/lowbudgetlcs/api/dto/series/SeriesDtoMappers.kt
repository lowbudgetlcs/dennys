package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.events.Stage
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.team.toTeamId

fun Series.toDto(): SeriesDto =
    SeriesDto(
        id = id.value,
        eventId = eventId.value,
        teamIds = participants.map { it.value },
        totalGames = totalGames,
        stage = stage,
    )

fun NewSeriesDto.toNewSeries(eventId: Int): NewSeries =
    NewSeries(
        eventId = eventId.toEventId(),
        totalGames = totalGames,
        participantIds = listOf(team1Id.toTeamId(), team2Id.toTeamId()),
        stage = stage.toStage(),
    )

fun String.toStage(): Stage =
    try {
        enumValueOf<Stage>(this)
    } catch (_: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid stage.")
    }
