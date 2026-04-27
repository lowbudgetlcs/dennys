package com.lowbudgetlcs.domain.team

import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.TeamQuery
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.TeamWithPlayers
import com.lowbudgetlcs.domain.team.models.types.TeamId

interface ITeamService {
    fun getAllTeams(query: TeamQuery? = null): List<Team>

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
