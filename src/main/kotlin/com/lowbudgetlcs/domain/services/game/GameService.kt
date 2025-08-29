package com.lowbudgetlcs.domain.services.game

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.toShortcode
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.*
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository

class GameService(
    private val gameRepo: IGameRepository,
    private val teamRepo: ITeamRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val gate: IRiotTournamentGateway
) : IGameService {
    override suspend fun createGame(newGame: NewGame): Game {
        // validate teams exist
        teamRepo.getById(newGame.blueTeamId)
            ?: throw NoSuchElementException("Team with id ${newGame.blueTeamId.value} not found.")
        teamRepo.getById(newGame.redTeamId)
            ?: throw NoSuchElementException("Team with id ${newGame.redTeamId.value} not found.")
        // validate + get series id
        val series = seriesRepo.getByParticipantIds(newGame.blueTeamId, newGame.redTeamId)
            ?: throw NoSuchElementException("Series with selected teams not found.")
        // Get tournament id
        val tid = eventRepo.getById(series.eventId)?.riotTournamentId
            ?: throw DatabaseException("Series found with no parent event.")
        // fetch shortcode
        val shortcode =
            gate.getCode(tid, NewShortcode())?.codes?.first() ?: throw GatewayException("Failed to create shortcode.")
        // insert data
        return gameRepo.insert(newGame, shortcode.toShortcode(), series.id)
            ?: throw DatabaseException("Failed to save game.")
    }
}