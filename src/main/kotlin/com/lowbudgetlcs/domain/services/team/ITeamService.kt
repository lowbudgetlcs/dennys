package com.lowbudgetlcs.domain.services.team

import com.lowbudgetlcs.domain.models.player.PlayerId
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.TeamUpdate
import com.lowbudgetlcs.domain.models.team.TeamWithPlayers

interface ITeamService {
    fun getAllTeams(): List<Team>

    fun getTeam(id: TeamId): Team

    fun createTeam(team: NewTeam): Team

    fun patchTeam(
        teamId: TeamId,
        patch: TeamUpdate,
    ): Team

    fun getTeamWithPlayers(id: TeamId): TeamWithPlayers

    fun addPlayerToTeam(
        playerId: PlayerId,
        teamId: TeamId,
    ): TeamWithPlayers

    fun removePlayerFromTeam(
        playerId: PlayerId,
        teamId: TeamId,
    ): TeamWithPlayers
}
