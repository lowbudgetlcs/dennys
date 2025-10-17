package com.lowbudgetlcs.domain.services.team

import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId

interface ITeamService {
    fun getAllTeams(): List<Team>

    fun getTeam(id: TeamId): Team

    fun createTeam(team: NewTeam): Team

    fun renameTeam(
        id: TeamId,
        newName: String,
    ): Team

    fun updateLogoName(
        id: TeamId,
        newLogoName: String?,
    ): Team
}
