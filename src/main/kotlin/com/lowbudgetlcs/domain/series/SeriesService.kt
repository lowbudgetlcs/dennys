package com.lowbudgetlcs.domain.series

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.game.models.filterCompleted
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.equalsIgnoreOrder
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SeriesService(
    private val gameRepo: IGameRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val teamRepo: ITeamRepository,
) : ISeriesService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createSeries(series: NewSeries): Series {
        logger.info("Creating new series...")
        logger.debug(series.toString())
        require(series.totalGames > 0) { "A series must contain at least 1 game." }
        val validate = { id: TeamId ->
            logger.debug("Validating team '$id' exists and is participating in event '${series.eventId}'...")
            val team = teamRepo.getById(id) ?: throw NoSuchElementException("Team with id $id not found")
            check(team.eventId == series.eventId) { "Team with id $id is not part of the event" }
        }
        validate(series.participantIds.first)
        validate(series.participantIds.second)

        return seriesRepo.insert(series) ?: throw DatabaseException("Failed to create series")
    }

    override fun getAllSeriesFromEvent(id: EventId): List<Series> {
        logger.debug("Fetching all series in event '$id'...")
        return seriesRepo.getAllByEventId(id)
    }

    override fun getSeries(id: SeriesId): Series {
        logger.info("Fetching series '$id'...")
        return seriesRepo.getById(id) ?: throw NoSuchElementException("Series not found")
    }

    override fun findSeries(
        eventId: EventId,
        teamId1: TeamId,
        teamId2: TeamId,
        eventStage: EventStage,
    ): Series {
        logger.info("Fetching series containing ('$teamId1', '$teamId2') in stage '$eventStage'...")
        eventRepo.getById(eventId) ?: throw NoSuchElementException("Event with id ${eventId.value} not found")
        val t1 = teamRepo.getById(teamId1) ?: throw NoSuchElementException("Team with id '${teamId1.value}' not found")
        val t2 = teamRepo.getById(teamId2) ?: throw NoSuchElementException("Team with id '${teamId2.value}' not found")
        // TODO: Make eventId non-null.
        require(t1.eventId == t2.eventId && t1.eventId != null) { "Teams are not in the same event." }

        val series =
            seriesRepo
                .getAllByEventId(eventId)
                .filter { it.eventStage == eventStage }
                .filter { it.participants == listOf(teamId1, teamId2) }

        if (series.size > 1) {
            throw DatabaseException("More than one series matched this filter.")
        } else if (series.isEmpty()) {
            throw NoSuchElementException("No series matched this filter.")
        }
        return series.first()
    }

    override fun removeSeries(id: SeriesId) {
        logger.info("Deleting series '$id'...")
        try {
            return seriesRepo.delete(id)
        } catch (e: Throwable) {
            throw DatabaseException("Failed to remove series")
        }
    }

    override fun completeSeries(result: SeriesResult): Series {
        logger.info("Completing series ${result.seriesId}...")
        logger.debug(result.toString())
        val series =
            seriesRepo.getById(result.seriesId)
                ?: throw NoSuchElementException("Series with id ${result.seriesId.value} not found.")
        logger.debug(series.toString())
        require(
            Pair(result.winningTeamId, result.losingTeamId).equalsIgnoreOrder(series.participants),
        ) { "Invalid team ids passed with series ${result.seriesId.value}." }
        return if (series.result == null)
            seriesRepo.insertResult(result) ?: throw DatabaseException("Failed to complete series.")
        else seriesRepo.overwriteResult(result) ?: throw DatabaseException("Failed to complete series.")
    }

    override fun isSeriesCompleted(seriesId: SeriesId): Boolean {
        logger.info("Checking if series $seriesId is complete...")
        val series =
            seriesRepo.getById(seriesId) ?: throw NoSuchElementException("Series with id ${seriesId.value} not found.")
        val games = gameRepo.getBySeriesId(seriesId).filterCompleted().groupBy { it.result!!.winningTeamId }
        // totalGames = 3, 3 / 2 == 1 + 1 == 2
        return games.any { it.key.value >= series.totalGames / 2 + 1 }
    }
}
