package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.TeamLogoName
import com.lowbudgetlcs.domain.models.team.TeamName
import com.lowbudgetlcs.domain.models.team.TeamWithPlayers

interface ITeamRepository {
    fun insert(newTeam: NewTeam): Team?
    fun getAll(): List<Team>
    fun getById(id: TeamId): Team?
    fun updateTeamName(id: TeamId, newName: TeamName): Team?
    fun updateTeamLogoName(id: TeamId, newLogoName: TeamLogoName): Team?

    fun insertPlayerToTeam(teamId: TeamId, playerId: PlayerId): TeamWithPlayers?
    fun removePlayer(teamId: TeamId, playerId: PlayerId): TeamWithPlayers?
}