package com.lowbudgetlcs.domain.services.series

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SeriesService(
    private val seriesRepository: ISeriesRepository,
    private val teamRepository: ITeamRepository,
) : ISeriesService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createSeries(series: NewSeries): Series {
        logger.debug("Creating new series...")
        logger.debug(series.toString())

        if (series.participantIds.count() <
            2
        ) {
            throw IllegalArgumentException("Series must have at least two participants")
        }

        if (series.gamesToWin < 1) throw IllegalArgumentException("Games to win must be at least 1")

        series.participantIds.forEach { id ->
            logger.debug("Validating team '$id' exists and is participating in event '${series.eventId}'...")
            val team = teamRepository.getById(id) ?: throw NoSuchElementException("Team with id $id not found")

            if (team.eventId != series.eventId) {
                throw IllegalArgumentException("Team with id $id is not part of the event")
            }
        }

        return seriesRepository.insert(series) ?: throw DatabaseException("Failed to create series")
    }

    override fun getAllSeriesFromEvent(id: EventId): List<Series> {
        logger.debug("Fetching all series in event '$id'...")
        return seriesRepository.getAllByEventId(id)
    }

    override fun getSeries(id: SeriesId): Series {
        logger.debug("Fetching series '$id'...")
        return seriesRepository.getById(id) ?: throw NoSuchElementException("Series not found")
    }

    override fun removeSeries(id: SeriesId) {
        logger.debug("Deleting series '$id'...")
        try {
            return seriesRepository.delete(id)
        } catch (e: Throwable) {
            throw DatabaseException("Failed to remove series")
        }
    }
}
