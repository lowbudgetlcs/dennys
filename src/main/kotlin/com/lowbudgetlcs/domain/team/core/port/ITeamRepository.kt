package com.lowbudgetlcs.domain.team.core.port

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName

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
