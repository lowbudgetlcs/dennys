package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Team
import com.lowbudgetlcs.models.TeamGameData
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.IEntityRepository
import kotlinx.serialization.Serializable

interface ITeamRepository : IEntityRepository<Team, TeamId> {
    /**
     * Saves [data] owned by [team] derived from [game] to storage. Returns [team] with
     * the new data if it succeeds, null otherwise.
     */
    fun saveTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team?
}