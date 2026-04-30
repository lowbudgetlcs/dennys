package com.lowbudgetlcs.domain.team.core.port

import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.team.core.models.NewTeam
import com.lowbudgetlcs.domain.team.core.models.Team
import com.lowbudgetlcs.domain.team.core.models.TeamQuery
import com.lowbudgetlcs.domain.team.core.models.TeamUpdate
import com.lowbudgetlcs.domain.team.core.models.TeamWithPlayers
import com.lowbudgetlcs.domain.team.core.models.types.TeamId

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
