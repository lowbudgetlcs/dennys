package com.lowbudgetlcs.domain.services.game

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.toShortcode
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
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
    private val teamRepo: ITeamRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val gate: IRiotTournamentGateway,
) : IGameService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun createGame(newGame: NewGame): Game {
        logger.debug("Creating new game...")
        logger.debug(newGame.toString())
        listOf(newGame.blueTeamId, newGame.redTeamId).forEach { teamId ->
            doesTeamExist(teamId) ?: throw NoSuchElementException("Team with id ${teamId.value} not found.")
        }
        logger.debug("Fetching series between '${newGame.blueTeamId}' and '${newGame.redTeamId}'...")
        val series =
            seriesRepo.getByParticipantIds(newGame.blueTeamId, newGame.redTeamId)
                ?: throw NoSuchElementException(
                    "Series with ${newGame.blueTeamId.value} and ${newGame.redTeamId.value} teams not found.",
                )
        logger.debug("Fetching tournament id for event '${series.eventId}'...t add")
        val event =
            eventRepo.getById(series.eventId)
                ?: throw DatabaseException("Series with id '${series.id}' does not have parent event.")
        val response =
            gate.getCode(event.riotTournamentId, NewShortcode())
                ?: throw GatewayException("Failed to create shortcode.")
        val shortcode = response.codes.first()
        return gameRepo.insert(newGame, shortcode.toShortcode(), series.id)
            ?: throw DatabaseException("Failed to save game.")
    }

    private fun doesTeamExist(teamId: TeamId): Team? {
        logger.debug("Checking if team '$teamId' exists...")
        return teamRepo.getById(teamId)
    }
}
