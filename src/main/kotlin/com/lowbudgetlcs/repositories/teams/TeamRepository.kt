package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Team
import com.lowbudgetlcs.models.TeamGameData
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.Repository
import kotlinx.serialization.Serializable
import migrations.Team_game_data
import migrations.Teams

@Serializable
data class TeamPerformanceId(val id: Int)

interface TeamRepository : Repository<Team, TeamId> {
    fun createTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team

    fun Teams.toTeam(): Team
    fun Team_game_data.toTeamGameData(): TeamGameData
}