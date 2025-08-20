package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.ITeamRepository

class TeamService(
    private val teamRepository: ITeamRepository
) {

    fun createTeam(team: NewTeam): Team {
        val name = team.name.value
        if (name.isBlank()) throw IllegalArgumentException("Team name cannot be blank")
        if (name.length > 80) throw IllegalArgumentException("Team name must be <= 80 characters")

        return teamRepository.insert(team)
            ?: throw RepositoryException("Failed to create team")
    }

    fun getAllTeams(): List<Team> = teamRepository.getAll()

    fun getTeam(id: TeamId): Team =
        teamRepository.getById(id) ?: throw NoSuchElementException("Team not found")

    fun renameTeam(id: TeamId, newName: String): Team {
        if (newName.isBlank()) throw IllegalArgumentException("Team name cannot be blank")
        if (newName.length > 80) throw IllegalArgumentException("Team name must be <= 80 characters")

        return teamRepository.updateTeamName(id, TeamName(newName))
            ?: throw RepositoryException("Failed to rename team")
    }

    fun updateLogoName(id: TeamId, newLogoName: String?): Team {
        val value = newLogoName ?: throw IllegalArgumentException("Logo name cannot be null")
        return teamRepository.updateTeamLogoName(id, TeamLogoName(value))
            ?: throw RepositoryException("Failed to update team logo")
    }
}