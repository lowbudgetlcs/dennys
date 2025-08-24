package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.team.*
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.ISeriesRepository
import com.lowbudgetlcs.repositories.ITeamRepository

class SeriesService(
        private val seriesRepository: ISeriesRepository,
        private val teamRepository: ITeamRepository
) : ISeriesService {

    override fun createSeries(series: NewSeries): Series {
        if (series.participantIds.count() < 2)
                throw IllegalArgumentException("Series must have at least two participants")

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

        return seriesRepository.insert(series) ?: throw DatabaseException("Failed to create team")
    }

    override fun getAllSeries(): List<Series> = seriesRepository.getAll()

    override fun getSeries(id: SeriesId): Series =
            seriesRepository.getById(id) ?: throw NoSuchElementException("Series not found")

    override fun removeSeries(id: SeriesId): Series {
        return seriesRepository.removeSeries(id)
                ?: throw DatabaseException("Failed to remove series")
    }
}
