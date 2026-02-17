package com.lowbudgetlcs.domain.services.series

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.ShortcodeOptions
import com.lowbudgetlcs.domain.models.events.Stage
import com.lowbudgetlcs.domain.models.events.toShortcode
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.equalsIgnoreOrder
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
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
    private val gate: IRiotTournamentGateway,
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

        if (series.totalGames < 1) throw IllegalArgumentException("A series must contain at least 1 game.")

        series.participantIds.forEach { id ->
            logger.debug("Validating team '$id' exists and is participating in event '${series.eventId}'...")
            val team = teamRepo.getById(id) ?: throw NoSuchElementException("Team with id $id not found")

            if (team.eventId != series.eventId) {
                throw IllegalArgumentException("Team with id $id is not part of the event")
            }
        }

        return seriesRepo.insert(series) ?: throw DatabaseException("Failed to create series")
    }

    override fun getAllSeriesFromEvent(id: EventId): List<Series> {
        logger.debug("Fetching all series in event '$id'...")
        return seriesRepo.getAllByEventId(id)
    }

    override fun getSeries(id: SeriesId): Series {
        logger.debug("Fetching series '$id'...")
        return seriesRepo.getById(id) ?: throw NoSuchElementException("Series not found")
    }

    override fun findSeries(
        eventId: EventId,
        teamId1: TeamId,
        teamId2: TeamId,
        stage: Stage,
    ): Series {
        logger.debug("Fetching series containing ('$teamId1', '$teamId2') in stage '$stage'...")
        eventRepo.getById(eventId) ?: throw NoSuchElementException("Event with id ${eventId.value} not found")
        val t1 =
            teamRepo.getById(teamId1) ?: throw NoSuchElementException("Team with id '${teamId1.value}' not found")
        val t2 =
            teamRepo.getById(teamId2) ?: throw NoSuchElementException("Team with id '${teamId2.value}' not found")
        // TODO: Make eventId non-null.
        if (t1.eventId != t2.eventId ||
            t1.eventId == null
        ) {
            throw IllegalArgumentException("Teams are not in the same event.")
        }

        val series =
            seriesRepo
                .getAllByEventId(eventId)
                .filter { it.stage == stage }
                .filter { it.participants == listOf(teamId1, teamId2) }

        if (series.size > 1) {
            throw DatabaseException("More than one series matched this filter.")
        } else if (series.isEmpty()) {
            throw NoSuchElementException("No series matched this filter.")
        }
        return series.first()
    }

    override fun removeSeries(id: SeriesId) {
        logger.debug("Deleting series '$id'...")
        try {
            return seriesRepo.delete(id)
        } catch (e: Throwable) {
            throw DatabaseException("Failed to remove series")
        }
    }

    override suspend fun createGame(newGame: NewGame): Game {
        logger.debug("Creating new game...")
        logger.debug(newGame.toString())
        val series = getSeries(newGame.seriesId) // Throws if not found
        val blueTeam =
            teamRepo.getById(newGame.blueTeamId)
                ?: throw NoSuchElementException("Team with id ${newGame.blueTeamId.value} not found")
        val redTeam =
            teamRepo.getById(newGame.redTeamId)
                ?: throw NoSuchElementException("Team with id ${newGame.redTeamId.value} not found")
        if (listOf(
                redTeam,
                blueTeam,
            ).equalsIgnoreOrder(series.participants)
        ) {
            throw IllegalArgumentException(
                "Provided teams are not part of series with id ${series.id.value}.",
            )
        }
        logger.debug("Fetching tournament id for event '${series.eventId}'...t add")
        val event =
            eventRepo.getById(series.eventId)
                ?: throw DatabaseException("Series with id '${series.id}' does not have parent event.")
        val response =
            gate.getCode(event.riotTournamentId, ShortcodeOptions())
                ?: throw GatewayException("Failed to create tournament code.")
        val shortcode = response.codes.first()
        return gameRepo.insert(newGame, shortcode.toShortcode())
            ?: throw DatabaseException("Failed to save game.")
    }
}
