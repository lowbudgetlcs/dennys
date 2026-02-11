package com.lowbudgetlcs.repositories.team

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.player.PlayerId
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.TeamUpdate

interface ITeamRepository {
    fun insert(newTeam: NewTeam): Team?

    fun getAll(): List<Team>

    fun getById(id: TeamId): Team?

    fun getByEventId(id: EventId): List<Team>

    fun update(
        team: Team,
        update: TeamUpdate,
    ): Team?

    fun insertPlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team?

    fun deletePlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team?
}
