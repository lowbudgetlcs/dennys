package com.lowbudgetlcs.domain.services.team

import com.lowbudgetlcs.domain.models.team.*
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.ITeamRepository

class TeamService(
    private val teamRepository: ITeamRepository
) : ITeamService {
    override fun getAllTeams(): List<Team> = teamRepository.getAll()

    override fun getTeam(id: TeamId): Team =
        teamRepository.getById(id) ?: throw NoSuchElementException("Team not found")

    override fun createTeam(team: NewTeam): Team {
        val name = team.name.value
        if (name.isBlank()) throw IllegalArgumentException("Team name cannot be blank")

        return teamRepository.insert(team)
            ?: throw DatabaseException("Failed to create team")
    }

    override fun renameTeam(id: TeamId, newName: String): Team {
        if (newName.isBlank()) throw IllegalArgumentException("Team name cannot be blank")

        return teamRepository.updateTeamName(id, TeamName(newName))
            ?: throw DatabaseException("Failed to rename team")
    }

    override fun updateLogoName(id: TeamId, newLogoName: String?): Team {
        val value = newLogoName ?: throw IllegalArgumentException("Logo name cannot be null")
        return teamRepository.updateTeamLogoName(id, TeamLogoName(value))
            ?: throw DatabaseException("Failed to update team logo")
    }
}