package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Team
import com.lowbudgetlcs.models.TeamGameData
import com.lowbudgetlcs.models.TeamId

interface ITeamRepository {
    /* Fetch all teams from storage */
    fun getAll(): List<Team>

    /* Fetch a team via id, returns null if none found */
    fun get(id: TeamId): Team?

    /* Update a Team in storage, returns null if operation fails */
    fun update(entity: Team): Team?

    /**
     * Saves [data] owned by [team] derived from [game] to storage. Returns [team] with
     * the new data if it succeeds, null otherwise.
     */
    fun saveTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team?
}