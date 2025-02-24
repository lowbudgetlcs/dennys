package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.Team
import com.lowbudgetlcs.entities.TeamGameData
import com.lowbudgetlcs.entities.TeamId
import com.lowbudgetlcs.repositories.IEntityRepository
import kotlinx.serialization.Serializable

@Serializable
data class TeamPerformanceId(val id: Int)

interface ITeamRepository : IEntityRepository<Team, TeamId> {
    fun createTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team
}