package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*

interface ITeamRepository {
    fun insert(newTeam: NewTeam): Team?
    fun getAll(): List<Team>
    fun getById(id: TeamId): Team?
    fun updateTeamName(id: TeamId, newName: TeamName): Team?
    fun updateTeamLogoName(id: TeamId, newLogoName: TeamLogoName): Team?

    fun insertPlayerToTeam(teamId: TeamId, playerId: PlayerId): TeamWithPlayers?
    fun removePlayer(teamId: TeamId, playerId: PlayerId): TeamWithPlayers?
}