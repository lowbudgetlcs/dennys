package com.lowbudgetlcs.repositories.team

import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.patch
import com.lowbudgetlcs.domain.team.models.toTeamId
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.PLAYERS_TO_TEAM
import org.jooq.storage.tables.references.TEAMS
import org.jooq.storage.tables.references.TEAM_TO_SERIES

class TeamRepository(
    private val dsl: DSLContext,
) : ITeamRepository {
    override fun getAll(): List<Team> = selectTeams().fetch().mapNotNull(::rowToTeam)

    override fun getById(id: TeamId): Team? = selectTeams().where(TEAMS.ID.eq(id.value)).fetchOne(::rowToTeam)

    override fun getByEventId(id: EventId): List<Team> =
        selectTeams().where(TEAMS.EVENT_ID.eq(id.value)).fetch().mapNotNull(::rowToTeam)

    override fun getByPlayerId(id: PlayerId): List<Team> =
        selectTeams().join(PLAYERS_TO_TEAM).on(PLAYERS_TO_TEAM.TEAM_ID.eq(TEAMS.ID))
            .where(PLAYERS_TO_TEAM.PLAYER_ID.eq(id.value)).fetch().mapNotNull(::rowToTeam)

    override fun getBySeriesId(seriesId: SeriesId): List<Team> =
        dsl.select(TEAMS.ID, TEAMS.NAME, TEAMS.LOGO, TEAMS.EVENT_ID)
            .from(TEAMS.innerJoin(TEAM_TO_SERIES).on(TEAMS.ID.eq(TEAM_TO_SERIES.TEAM_ID)))
            .where(TEAM_TO_SERIES.SERIES_ID.eq(seriesId.value)).fetch().mapNotNull(::rowToTeam)

    override fun getByName(name: TeamName): List<Team> =
        selectTeams().where(TEAMS.NAME.eq(name.value)).fetch().mapNotNull(::rowToTeam)

    override fun insert(newTeam: NewTeam): Team? {
        val insertedId =
            dsl.insertInto(TEAMS).set(TEAMS.NAME, newTeam.name.value).returning(TEAMS.ID).fetchOne()?.get(TEAMS.ID)

        return insertedId?.toTeamId()?.let(::getById)
    }

    override fun update(
        team: Team,
        update: TeamUpdate,
    ): Team? {
        val patch = team.patch(update)
        val updatedId = dsl.update(TEAMS).set(TEAMS.NAME, patch.name.value).set(TEAMS.EVENT_ID, patch.eventId?.value)
            .set(TEAMS.LOGO, patch.logo).where(TEAMS.ID.eq(team.id.value)).returning(TEAMS.ID).fetchOne()?.get(TEAMS.ID)
        return updatedId?.toTeamId()?.let(::getById)
    }

    override fun insertPlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team? {
        val insertedId = dsl.insertInto(PLAYERS_TO_TEAM).set(PLAYERS_TO_TEAM.PLAYER_ID, playerId.value)
            .set(PLAYERS_TO_TEAM.TEAM_ID, teamId.value).returning(PLAYERS_TO_TEAM.TEAM_ID).fetchOne()
            ?.get(PLAYERS_TO_TEAM.TEAM_ID)
        return insertedId?.toTeamId()?.let(::getById)
    }

    override fun deletePlayerTeamLink(
        teamId: TeamId,
        playerId: PlayerId,
    ): Team? {
        val updated = dsl.delete(PLAYERS_TO_TEAM).where(PLAYERS_TO_TEAM.PLAYER_ID.eq(playerId.value))
            .and(PLAYERS_TO_TEAM.TEAM_ID.eq(teamId.value)).execute()

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
