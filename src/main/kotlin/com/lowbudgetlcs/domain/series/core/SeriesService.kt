package com.lowbudgetlcs.domain.series.core

import com.lowbudgetlcs.domain.RepositoryException
import com.lowbudgetlcs.domain.event.core.model.ShortcodeOptions
import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.event.core.model.types.toShortcode
import com.lowbudgetlcs.domain.event.core.port.IEventRepository
import com.lowbudgetlcs.domain.series.core.model.Game
import com.lowbudgetlcs.domain.series.core.model.NewGame
import com.lowbudgetlcs.domain.series.core.model.NewSeries
import com.lowbudgetlcs.domain.series.core.model.Series
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.series.core.port.IGameRepository
import com.lowbudgetlcs.domain.series.core.port.ISeriesRepository
import com.lowbudgetlcs.domain.series.core.port.ISeriesService
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
) : ISeriesService {

    override suspend fun createSeries(series: NewSeries): Series {
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

    override suspend fun getAllSeriesFromEvent(id: EventId): List<Series> {
        logger.debug("Fetching all series in event '$id'...")
        return seriesRepo.getAllByEventId(id)
    }

    override suspend fun getSeries(id: SeriesId): Series {
        logger.debug("Fetching series '$id'...")
        return seriesRepo.getById(id) ?: throw NoSuchElementException("Series not found")
    }

    override suspend fun findSeries(
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

        val series =
            seriesRepo
                .getAllByEventId(eventId)
                .filter { it.eventStage == eventStage }
                .filter { it.participants == listOf(teamId1, teamId2) }

        if (series.size > 1) {
            throw RepositoryException("More than one series matched this filter.")
        } else if (series.isEmpty()) {
            throw NoSuchElementException("No series matched this filter.")
        }
        return series.first()
    }

    override suspend fun removeSeries(id: SeriesId) {
        logger.debug("Deleting series '$id'...")
        try {
            return seriesRepo.delete(id)
        } catch (_: Throwable) {
            throw RepositoryException("Failed to remove series.")
        }
    }

    override suspend fun createGame(newGame: NewGame): Game {
        logger.debug("Creating new game...")
        logger.debug(newGame.toString())
        val series = getSeries(newGame.seriesId) // Throws if not found
        val blueTeam =
            teamRepo.getById(newGame.blueTeamId)
                ?: throw NoSuchElementException("Team with id ${newGame.blueTeamId.value} not found.")
        val redTeam =
            teamRepo.getById(newGame.redTeamId)
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
        val event =
            eventRepo.getById(series.eventId)
                ?: throw RepositoryException("Series with id '${series.id}' does not have parent event.")
        val response =
            gate.getCode(event.riotTournamentId, ShortcodeOptions())
                ?: throw GatewayException("Failed to create tournament code.")
        val shortcode = response.codes.first()
        return gameRepo.insert(newGame, shortcode.toShortcode()) ?: throw RepositoryException("Failed to save game.")
    }
}
