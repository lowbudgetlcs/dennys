package com.lowbudgetlcs.domain.services.team

import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.TeamLogoName
import com.lowbudgetlcs.domain.models.team.TeamName
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.team.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TeamService(
    private val teamRepository: ITeamRepository,
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

    override fun createTeam(team: NewTeam): Team {
        logger.debug("Creating new team...")
        logger.debug(team.toString())
        val name = team.name.value
        if (name.isBlank()) throw IllegalArgumentException("Team name cannot be blank")

        return teamRepository.insert(team)
            ?: throw DatabaseException("Failed to create team")
    }

    override fun renameTeam(
        id: TeamId,
        newName: String,
    ): Team {
        logger.debug("Renaming team '$id' to '$newName'...")
        if (newName.isBlank()) throw IllegalArgumentException("Team name cannot be blank")

        return teamRepository.updateTeamName(id, TeamName(newName))
            ?: throw DatabaseException("Failed to rename team")
    }

    override fun updateLogoName(
        id: TeamId,
        newLogoName: String?,
    ): Team {
        logger.debug("Changing team '$id' logoName to '$newLogoName'...")
        val value = newLogoName ?: throw IllegalArgumentException("Logo name cannot be null")
        return teamRepository.updateTeamLogoName(id, TeamLogoName(value))
            ?: throw DatabaseException("Failed to update team logo")
    }
}
