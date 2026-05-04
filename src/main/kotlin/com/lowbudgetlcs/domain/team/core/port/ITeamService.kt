package com.lowbudgetlcs.domain.team.core.port

import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamQuery
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.TeamWithPlayers
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

interface ITeamService {
    suspend fun getAllTeams(query: TeamQuery? = null): List<Team>

    suspend fun getTeam(id: TeamId): Team

    suspend fun createTeam(team: NewTeam): Team

    suspend fun patchTeam(
        teamId: TeamId,
        patch: TeamUpdate,
    ): Team

    suspend fun getTeamWithPlayers(id: TeamId): TeamWithPlayers

    suspend fun addPlayerToTeam(
        playerId: PlayerId,
        teamId: TeamId,
    ): TeamWithPlayers

    suspend fun removePlayerFromTeam(
        playerId: PlayerId,
        teamId: TeamId,
    ): TeamWithPlayers
}
