package com.lowbudgetlcs.domain.series.game

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.types.TeamId
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

class GameService(
    private val gameRepo: IGameRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val teamRepo: ITeamRepository,
    private val gate: IRiotTournamentGateway,
) : IGameService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun getSeriesById(seriesId: SeriesId): Series {
        logger.info("Getting series by id '${seriesId}'...")
        val series =
            seriesRepo.getById(seriesId)
                ?: throw NoSuchElementException("Series with id '${seriesId.value}' not found.")
        logger.debug(series.toString())
        return series
    }

    private fun getTeamById(teamId: TeamId): Team {
        logger.info("Getting team by id '${teamId}'...")
        val team =
            teamRepo.getById(teamId)
                ?: throw NoSuchElementException("Team with id ${teamId.value} not found")
        logger.debug(team.toString())
        return team
    }

    private fun getEventById(eventId: EventId): Event {
        logger.info("Getting event by id '${eventId}'...")
        val event = eventRepo.getById(eventId)
            ?: throw DatabaseException("Series with id '${eventId}' does not have parent event.")
        logger.debug(event.toString())
        return event
    }

    override fun getById(id: GameId): Game {
        logger.info("Getting game by id '${id}'...")
        val game =
            gameRepo.getById(id) ?: throw NoSuchElementException("No game found for ${id.value}.")
        logger.debug(game.toString())
        return game
    }

    override fun getByShortcode(shortcode: Shortcode): Game {
        logger.info("Getting game with shortcode: $shortcode")
        val game =
            gameRepo.getByShortcode(shortcode) ?: throw NoSuchElementException("No game found for ${shortcode.value}.")
        logger.debug(game.toString())
        return game
    }

    override suspend fun createGame(newGame: NewGame): Game {
        logger.info("Creating new game...")
        logger.debug(newGame.toString())
        val series = getSeriesById(newGame.seriesId)
        val blueTeam = getTeamById(newGame.blueTeamId)
        val redTeam = getTeamById(newGame.redTeamId)
        require(
            Pair(
                redTeam.id,
                blueTeam.id,
            ).equalsIgnoreOrder(series.participants),
        ) {
            "Provided teams are not part of series with id ${series.id.value}."
        }
        logger.debug("Fetching tournament id for event '${series.eventId}'...")
        val event = getEventById(series.eventId)
        val response =
            gate.getCode(event.riotTournamentId, ShortcodeOptions())
                ?: throw GatewayException("Failed to create tournament code.")
        val shortcode = response.codes.first()
        return gameRepo.insert(newGame, shortcode.toShortcode()) ?: throw DatabaseException("Failed to save game.")
    }

    override fun completeGame(result: GameResult): Game {
        logger.info("Completing game ${result.gameId}...")
        logger.debug(result.toString())
        val g =
            gameRepo.getById(result.gameId)
                ?: throw NoSuchElementException("Game with id ${result.gameId.value} not found.")
        require(
            listOf(g.blueTeamId, g.redTeamId).equalsIgnoreOrder(
                listOf(
                    result.winningTeamId,
                    result.losingTeamId,
                ),
            ),
        ) { "Invalid team ids passed with game ${result.gameId.value}." }
        return if (g.result == null)
            gameRepo.insertResult(result) ?: throw DatabaseException("Failed to complete game.")
        else gameRepo.overwriteResult(result) ?: throw DatabaseException("Failed to complete game.")
    }
}
