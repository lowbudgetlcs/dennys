package com.lowbudgetlcs.repositories.team

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName

interface ITeamRepository {
    fun insert(newTeam: NewTeam): Team?

    fun getAll(): List<Team>

    fun getById(id: TeamId): Team?

    fun getByEventId(id: EventId): List<Team>

    fun getByPlayerId(id: PlayerId): List<Team>

    fun getBySeriesId(seriesId: SeriesId): List<Team>

    fun getByName(name: TeamName): List<Team>

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
