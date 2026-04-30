package com.lowbudgetlcs.domain.team.core

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamQuery
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.TeamWithPlayers
import com.lowbudgetlcs.domain.team.core.model.filterByString
import com.lowbudgetlcs.domain.team.core.model.toTeamWithPlayers
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName
import com.lowbudgetlcs.domain.team.core.port.ITeamService
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TeamService(
    private val teamRepository: ITeamRepository,
    private val playerRepository: IPlayerRepository,
) : ITeamService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllTeams(query: TeamQuery?): List<Team> {
        logger.debug("Fetching all teams...")
        return teamRepository.getAll().filterByString(query)
    }

    override fun getTeam(id: TeamId): Team {
        logger.debug("Fetching team '$id'...")
        return teamRepository.getById(id) ?: throw NoSuchElementException("Team not found")
    }

    override fun getTeamWithPlayers(id: TeamId): TeamWithPlayers {
        logger.debug("Getting team by '$id' (with players)...")
        val team = getTeam(id)
        val players = playerRepository.getByTeamId(id)
        return team.toTeamWithPlayers(players)
    }

    override fun addPlayerToTeam(
        playerId: PlayerId,
        teamId: TeamId,
    ): TeamWithPlayers {
        logger.debug("Adding player '$playerId' to team '$teamId'...")
        // TODO: Is this addition legal?
        getTeam(teamId)
        getPlayer(playerId)
        teamRepository.insertPlayerTeamLink(teamId, playerId)
            ?: throw DatabaseException("Failed to add player '${playerId.value}' to team '${teamId.value}'.")
        return getTeamWithPlayers(teamId)
    }

    override fun removePlayerFromTeam(
        playerId: PlayerId,
        teamId: TeamId,
    ): TeamWithPlayers {
        logger.debug("Removing player '$playerId' from team '$teamId'...")
        getTeam(teamId)
        getPlayer(playerId)
        teamRepository.deletePlayerTeamLink(teamId, playerId)
            ?: throw DatabaseException("Failed to remove player '${playerId.value}' from team '${teamId.value}'.")
        return getTeamWithPlayers(teamId)
    }

    override fun createTeam(team: NewTeam): Team {
        logger.debug("Creating new team...")
        logger.debug(team.toString())
        val name = team.name.value
        require(name.isNotBlank()) { "Team name cannot be blank" }

        return teamRepository.insert(team) ?: throw DatabaseException("Failed to create team")
    }

    override fun patchTeam(
        teamId: TeamId,
        patch: TeamUpdate,
    ): Team {
        logger.debug("Patching team '$teamId'...")
        logger.debug(patch.toString())
        val team = getTeam(teamId)
        // Check if name is taken
        patch.name?.let { name ->
            check(!isNameTaken(name, team.eventId)) {
                "Team with name '${name.value}' (in event ${team.eventId?.value}) already taken."
            }
        }
        return teamRepository.update(team, patch) ?: throw DatabaseException("Failed to patch team.")
    }

    private fun getPlayer(id: PlayerId): Player {
        logger.debug("Fetching player '$id'...")
        val player = playerRepository.getById(id) ?: throw NoSuchElementException("Player '${id.value}' not found.")
        return player
    }

    fun isNameTaken(
        name: TeamName,
        eventId: EventId?,
    ): Boolean {
        logger.debug("Checking if $name is available...")
        return teamRepository.getByName(name).any { it.eventId == eventId }
    }
}
