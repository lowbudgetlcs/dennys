package com.lowbudgetlcs.domain.division.core.series.services

import com.lowbudgetlcs.domain.RepositoryException
import com.lowbudgetlcs.domain.division.core.event.model.ShortcodeOptions
import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.event.model.types.toShortcode
import com.lowbudgetlcs.domain.division.core.event.port.IEventRepository
import com.lowbudgetlcs.domain.division.core.series.model.Game
import com.lowbudgetlcs.domain.division.core.series.model.NewGame
import com.lowbudgetlcs.domain.division.core.series.model.NewSeries
import com.lowbudgetlcs.domain.division.core.series.model.Series
import com.lowbudgetlcs.domain.division.core.series.model.types.SeriesId
import com.lowbudgetlcs.domain.division.core.series.port.IGameRepository
import com.lowbudgetlcs.domain.division.core.series.port.ISeriesRepository
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import com.lowbudgetlcs.equalsIgnoreOrder
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.logger

class SeriesService(
    private val gameRepo: IGameRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val teamRepo: ITeamRepository,
    private val gate: IRiotTournamentGateway,
) {

    /**
     * Create an event from a NewSeries.
     *
     * @param series new series details.
     * @return the newly created Series.
     *
     * @throws IllegalArgumentException if the series cannot be created.
     * @throws com.lowbudgetlcs.domain.RepositoryException if the underlying repositories fail.
     */
    suspend fun createSeries(series: NewSeries): Series {
        logger.debug("Creating new series...")
        logger.debug(series.toString())
        require(series.totalGames > 0) { "A series must contain at least 1 game." }
        suspend fun validate(id: TeamId) {
            logger.debug("Validating team '$id' exists and is participating in event '${series.eventId}'...")
            val team = teamRepo.getById(id) ?: throw NoSuchElementException("Team with id $id not found")
            check(team.eventId == series.eventId) { "Team with id $id is not part of the event" }
        }
        validate(series.participantIds.first)
        validate(series.participantIds.second)

        return seriesRepo.insert(series) ?: throw RepositoryException("Failed to create series")
    }

    /** Fetches all series from an event. */
    suspend fun getAllSeriesFromEvent(id: EventId): List<Series> {
        logger.debug("Fetching all series in event '$id'...")
        return seriesRepo.getAllByEventId(id)
    }

    /**
     * Fetch a series by id.
     *
     * @param id the id of the series.
     * @return the specified series.
     *
     * @throws NoSuchElementException when the series is not found.
     * @throws com.lowbudgetlcs.domain.RepositoryException when the underlying repository fails.
     */
    suspend fun getSeries(id: SeriesId): Series {
        logger.debug("Fetching series '$id'...")
        return seriesRepo.getById(id) ?: throw NoSuchElementException("Series not found")
    }

    /**
     * Return a series given two TeamIds and an event Stage. Will throw if multiple series match.
     *
     * @param eventId the event to search.
     * @param teamId1 the first teamId to filter by.
     * @param teamId2 the second teamId to filter by.
     * @param eventStage the event stage to filter by.
     * @return a series containing both team ids inside the specified event stage.
     *
     * @throws NoSuchElementException when no series is found.
     * @throws com.lowbudgetlcs.domain.RepositoryException if >1 series is found.
     * @throws IllegalArgumentException when the teamIds are invalid.
     */
    suspend fun findSeries(
        eventId: EventId,
        teamId1: TeamId,
        teamId2: TeamId,
        eventStage: EventStage,
    ): Series {
        logger.debug("Fetching series containing ('{}', '{}') in stage '{}'...", teamId1, teamId2, eventStage)
        eventRepo.getById(eventId) ?: throw NoSuchElementException("Event with id ${eventId.value} not found")
        val t1 = teamRepo.getById(teamId1) ?: throw NoSuchElementException("Team with id '${teamId1.value}' not found")
        val t2 = teamRepo.getById(teamId2) ?: throw NoSuchElementException("Team with id '${teamId2.value}' not found")
        // TODO: Make eventId non-null.
        require(t1.eventId == t2.eventId && t1.eventId != null) { "Teams are not in the same event." }

        val series = seriesRepo.getAllByEventId(eventId).filter { it.eventStage == eventStage }
            .filter { it.participants == listOf(teamId1, teamId2) }

        if (series.size > 1) {
            throw RepositoryException("More than one series matched this filter.")
        } else if (series.isEmpty()) {
            throw NoSuchElementException("No series matched this filter.")
        }
        return series.first()
    }

    /**
     * Remove a series.
     *
     * @param SeriesId the target series.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     * @throws com.lowbudgetlcs.domain.RepositoryException if the delete operation fails
     */
    suspend fun removeSeries(id: SeriesId) {
        logger.debug("Deleting series '$id'...")
        try {
            return seriesRepo.delete(id)
        } catch (_: Throwable) {
            throw RepositoryException("Failed to remove series.")
        }
    }

    /**
     * Create a game inside of a series.
     *
     * @param NewGame the new game parameters.
     */
    suspend fun createGame(newGame: NewGame): Game {
        logger.debug("Creating new game...")
        logger.debug(newGame.toString())
        val series = getSeries(newGame.seriesId) // Throws if not found
        val blueTeam = teamRepo.getById(newGame.blueTeamId)
            ?: throw NoSuchElementException("Team with id ${newGame.blueTeamId.value} not found.")
        val redTeam = teamRepo.getById(newGame.redTeamId)
            ?: throw NoSuchElementException("Team with id ${newGame.redTeamId.value} not found.")
        require(
            Pair(
                redTeam,
                blueTeam,
            ).equalsIgnoreOrder(series.participants),
        ) {
            "Provided teams are not part of series with id ${series.id.value}."
        }
        logger.debug("Fetching tournament id for event '${series.eventId}'...")
        val event = eventRepo.getById(series.eventId)
            ?: throw RepositoryException("Series with id '${series.id}' does not have parent event.")
        val response = gate.getCode(event.riotTournamentId, ShortcodeOptions())
            ?: throw GatewayException("Failed to create tournament code.")
        val shortcode = response.codes.first()
        return gameRepo.insert(newGame, shortcode.toShortcode()) ?: throw RepositoryException("Failed to save game.")
    }
}
