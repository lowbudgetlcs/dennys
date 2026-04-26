package com.lowbudgetlcs.domain.series.game

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
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

    override fun getByShortcode(shortcode: Shortcode): Game =
        gameRepo.getByShortcode(shortcode) ?: throw NoSuchElementException("No game found for ${shortcode.value}.")

    override suspend fun createGame(newGame: NewGame): Game {
        logger.info("Creating new game...")
        logger.debug(newGame.toString())
        val series =
            seriesRepo.getById(newGame.seriesId)
                ?: throw NoSuchElementException("Series with id '${newGame.seriesId.value}' not found.")
        val blueTeam =
            teamRepo.getById(newGame.blueTeamId)
                ?: throw NoSuchElementException("Team with id ${newGame.blueTeamId.value} not found")
        val redTeam =
            teamRepo.getById(newGame.redTeamId)
                ?: throw NoSuchElementException("Team with id ${newGame.redTeamId.value} not found")
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
                ?: throw DatabaseException("Series with id '${series.id}' does not have parent event.")
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
        return gameRepo.insertGameResult(result) ?: throw DatabaseException("Failed to complete game.")
    }
}
