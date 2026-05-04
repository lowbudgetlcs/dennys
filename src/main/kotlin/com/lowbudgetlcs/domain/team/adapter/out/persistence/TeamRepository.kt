package com.lowbudgetlcs.domain.team.adapter.out.persistence

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.event.core.model.types.toEventId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.patch
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import com.lowbudgetlcs.domain.team.core.model.types.toTeamName
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.PLAYERS_TO_TEAM
import org.jooq.storage.tables.references.TEAMS
import org.jooq.storage.tables.references.TEAM_TO_SERIES

class TeamRepository(
    private val dsl: DSLContext,
) : ITeamRepository {
    override suspend fun getAll(): List<Team> = withContext(Dispatchers.IO) {
        selectTeams().fetch()
    }.mapNotNull(::rowToTeam)

    override suspend fun getById(id: TeamId): Team? =
        withContext(Dispatchers.IO) {
            selectTeams().where(TEAMS.ID.eq(id.value)).fetchOne(::rowToTeam)
        }

    override suspend fun getByEventId(id: EventId): List<Team> =
        withContext(Dispatchers.IO) {
            selectTeams().where(TEAMS.EVENT_ID.eq(id.value)).fetch()
        }.mapNotNull(::rowToTeam)

    override suspend fun getByPlayerId(id: PlayerId): List<Team> =
        withContext(Dispatchers.IO) {
            selectTeams().join(PLAYERS_TO_TEAM).on(PLAYERS_TO_TEAM.TEAM_ID.eq(TEAMS.ID))
                .where(PLAYERS_TO_TEAM.PLAYER_ID.eq(id.value)).fetch()
        }.mapNotNull(::rowToTeam)

    override suspend fun getBySeriesId(seriesId: SeriesId): List<Team> =
        withContext(Dispatchers.IO) {
            dsl.select(TEAMS.ID, TEAMS.NAME, TEAMS.LOGO, TEAMS.EVENT_ID)
                .from(TEAMS.innerJoin(TEAM_TO_SERIES).on(TEAMS.ID.eq(TEAM_TO_SERIES.TEAM_ID)))
                .where(TEAM_TO_SERIES.SERIES_ID.eq(seriesId.value)).fetch()
        }.mapNotNull(::rowToTeam)

    override suspend fun getByName(name: TeamName): List<Team> =
        withContext(Dispatchers.IO) {
            selectTeams().where(TEAMS.NAME.eq(name.value)).fetch()
        }.mapNotNull(::rowToTeam)

    override suspend fun insert(newTeam: NewTeam): Team? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl.insertInto(TEAMS).set(TEAMS.NAME, newTeam.name.value).returning(TEAMS.ID).fetchOne()
            }?.get(TEAMS.ID)

        return insertedId?.toTeamId()?.let { getById(it) }
    }

    override suspend fun update(
        team: Team,
        update: TeamUpdate,
    ): Team? {
        val patch = team.patch(update)
        val updatedId = withContext(Dispatchers.IO) {
            dsl.update(TEAMS).set(TEAMS.NAME, patch.name.value).set(TEAMS.EVENT_ID, patch.eventId?.value)
                .set(TEAMS.LOGO, patch.logo).where(TEAMS.ID.eq(team.id.value)).returning(TEAMS.ID).fetchOne()
        }?.get(TEAMS.ID)
        return updatedId?.toTeamId()?.let { getById(it) }
    }

    override suspend fun insertPlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team? {
        val insertedId = withContext(Dispatchers.IO) {
            dsl.insertInto(PLAYERS_TO_TEAM).set(PLAYERS_TO_TEAM.PLAYER_ID, playerId.value)
                .set(PLAYERS_TO_TEAM.TEAM_ID, teamId.value).returning(PLAYERS_TO_TEAM.TEAM_ID).fetchOne()
        }
            ?.get(PLAYERS_TO_TEAM.TEAM_ID)
        return insertedId?.toTeamId()?.let { getById(it) }
    }

    override suspend fun deletePlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team? {
        val updated = withContext(Dispatchers.IO) {
            dsl.delete(PLAYERS_TO_TEAM).where(PLAYERS_TO_TEAM.PLAYER_ID.eq(playerId.value))
                .and(PLAYERS_TO_TEAM.TEAM_ID.eq(teamId.value)).execute()
        }

        return if (updated > 0) getById(teamId) else null
    }

    // Helper functions

    private fun selectTeams() = dsl.select(TEAMS.ID, TEAMS.NAME, TEAMS.LOGO, TEAMS.EVENT_ID).from(TEAMS)

    private fun rowToTeam(row: Record): Team? {
        val teamId = row[TEAMS.ID]?.toTeamId() ?: return null
        val name = row[TEAMS.NAME]?.toTeamName() ?: return null
        val logo = row[TEAMS.LOGO]
        val eventId = row[TEAMS.EVENT_ID]?.toEventId()

        return Team(
            id = teamId,
            name = name,
            logo = logo,
            eventId = eventId,
        )
    }
}
