package com.lowbudgetlcs.domain.services.series

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository

class SeriesService(
    private val seriesRepository: ISeriesRepository, private val teamRepository: ITeamRepository
) : ISeriesService {

    override fun createSeries(series: NewSeries): Series {
        if (series.participantIds.count() < 2) throw IllegalArgumentException("Series must have at least two participants")

        if (series.gamesToWin < 1) throw IllegalArgumentException("Games to win must be at least 1")

        series.participantIds.forEach { id ->
            val team = teamRepository.getById(id) ?: throw NoSuchElementException("Team with id $id not found")

            if (team.eventId != series.eventId) {
                throw IllegalArgumentException("Team with id $id is not part of the event")
            }
        }

        return seriesRepository.insert(series) ?: throw DatabaseException("Failed to create series")
    }

    override fun getAllSeriesFromEvent(id: EventId): List<Series> = seriesRepository.getAllByEventId(id)

    override fun getSeries(id: SeriesId): Series =
        seriesRepository.getById(id) ?: throw NoSuchElementException("Series not found")

    override fun removeSeries(id: SeriesId) {
        try {
            return seriesRepository.delete(id)
        } catch (e: Throwable) {
            throw DatabaseException("Failed to remove series")
        }
    }
}