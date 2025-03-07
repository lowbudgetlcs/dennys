package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Team
import com.lowbudgetlcs.models.TeamGameData
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.IRepository
import com.lowbudgetlcs.repositories.IUniqueRepository

interface ITeamRepository : IUniqueRepository<Team, TeamId>, IRepository<Team> {
    /**
     * Saves [data] owned by [team] derived from [game] to storage. Returns [team] with
     * the new data if it succeeds, null otherwise.
     */
    fun saveTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team?
}