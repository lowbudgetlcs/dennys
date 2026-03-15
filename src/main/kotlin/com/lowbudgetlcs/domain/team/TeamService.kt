package com.lowbudgetlcs.domain.team

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.team.models.*
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TeamService(
    private val teamRepository: ITeamRepository,
    private val playerRepository: IPlayerRepository,
) : ITeamService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllTeams(): List<Team> {
        logger.debug("Fetching all teams...")
        return teamRepository.getAll()
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
        require(name.isBlank()) { "Team name cannot be blank" }

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
            require(isNameTaken(name, team.eventId)) {
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
