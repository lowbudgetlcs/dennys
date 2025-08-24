package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.team.*
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.ISeriesRepository
import com.lowbudgetlcs.repositories.ITeamRepository

class SeriesService(
    private val seriesRepository: ISeriesService,
    private val teamRepository: ITeamRepository
) {

    fun createSeries(series: NewSeries): Series {
        if (series.participantIds < 2) throw IllegalArgumentException("Series must have at least two participants")

        if (series.gamesToWin < 1) throw IllegalArgumentException("Games to win must be at least 1")

        series.participantIds.forEach { id ->
            val team = teamRepository.getById(id)

            if (team == null) {
                throw IllegalArgumentException("Team with id $id not found")
            }

            if (team.eventId != series.eventId) {
                throw IllegalArgumentException("Team with id $id is not part of the event")
            }
        }

        if (name.isBlank()) throw IllegalArgumentException("Team name cannot be blank")

        return teamRepository.insert(team)
            ?: throw DatabaseException("Failed to create team")
    }

    fun getAllSeries(): List<Series> = seriesRepository.getAll()

    fun getSeries(id: SeriesId): Series =
        seriesRepository.getById(id) ?: throw NoSuchElementException("Series not found")

}
