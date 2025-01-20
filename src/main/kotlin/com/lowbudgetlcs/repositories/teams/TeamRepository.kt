package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.repositories.Repository
import kotlinx.serialization.Serializable
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType
import no.stelar7.api.r4j.pojo.lol.match.v5.ObjectiveStats

interface TeamRepository : Repository<Team, TeamId> {
    @Serializable
    data class TeamPerformance(
        val gameId: GameId, val divisionId: DivisionId
    )

    fun createTeamData(
        team: Team,
        game: Game,
        side: TeamType,
        win: Boolean,
        gold: Int,
        objectives: Map<String, ObjectiveStats>,
    ): Team
}