package com.lowbudgetlcs.domain.team.core.port

import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName

interface ITeamRepository {
    suspend fun insert(newTeam: NewTeam): Team?

    suspend fun getAll(): List<Team>

    suspend fun getById(id: TeamId): Team?

    suspend fun getByEventId(id: EventId): List<Team>

    suspend fun getByPlayerId(id: PlayerId): List<Team>

    suspend fun getBySeriesId(seriesId: SeriesId): List<Team>

    suspend fun getByName(name: TeamName): List<Team>

    suspend fun update(
        team: Team,
        update: TeamUpdate,
    ): Team?

    suspend fun insertPlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team?

    suspend fun deletePlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team?
}
