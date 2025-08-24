package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.Player
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.TeamLogoName
import com.lowbudgetlcs.domain.models.team.TeamName
import com.lowbudgetlcs.domain.models.team.TeamWithPlayers
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.team.toTeamLogoName
import com.lowbudgetlcs.domain.models.team.toTeamName
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENTS
import org.jooq.storage.tables.references.PLAYERS
import org.jooq.storage.tables.references.PLAYERS_TO_TEAM
import org.jooq.storage.tables.references.TEAMS

class TeamRepository(
    private val dsl: DSLContext
) : ITeamRepository {

    override fun insert(newTeam: NewTeam): Team? {
        val insertedId =
            dsl.insertInto(TEAMS).set(TEAMS.NAME, newTeam.name.value).set(TEAMS.LOGO_NAME, newTeam.logoName?.value)
                .returning(TEAMS.ID).fetchOne()?.get(TEAMS.ID)

        return insertedId?.toTeamId()?.let(::getById)
    }

    override fun getAll(): List<Team> {
        return fetchTeamRows().mapNotNull(::rowToTeam)
    }

    override fun getById(id: TeamId): Team? {
        return getTeamRowById(id)?.let(::rowToTeam)
    }

    override fun updateTeamName(id: TeamId, newName: TeamName): Team? {
        val updated = dsl.update(TEAMS).set(TEAMS.NAME, newName.value).where(TEAMS.ID.eq(id.value)).execute()

        return if (updated > 0) getById(id) else null
    }

    override fun updateTeamLogoName(id: TeamId, newLogoName: TeamLogoName): Team? {
        val updated = dsl.update(TEAMS).set(TEAMS.LOGO_NAME, newLogoName.value).where(TEAMS.ID.eq(id.value)).execute()

        return if (updated > 0) getById(id) else null
    }

    override fun updateEventId(id: TeamId, eventId: EventId?): Team? {
        val insertedId =
            dsl.update(TEAMS).set(TEAMS.EVENT_ID, eventId?.value).where(TEAMS.ID.eq(id.value)).returning(TEAMS.ID)
                .fetchOne()?.get(TEAMS.ID)
        return insertedId?.toTeamId()?.let(::getById)
    }

    override fun insertPlayerToTeam(teamId: TeamId, playerId: PlayerId): TeamWithPlayers? {
        val u = dsl.update(PLAYERS_TO_TEAM).set(PLAYERS_TO_TEAM.TEAM_ID, teamId.value)
            .set(PLAYERS_TO_TEAM.PLAYER_ID, playerId.value)
        val updated = dsl.update(PLAYERS.playersToTeam).set(PLAYERS.playersToTeam.TEAM_ID, teamId.value)
            .where(PLAYERS.playersToTeam.PLAYER_ID.eq(playerId.value)).execute()

        return if (updated > 0) getByTeamWithPlayersId(teamId) else null
    }

    override fun removePlayer(teamId: TeamId, playerId: PlayerId): TeamWithPlayers? {
        val updated = dsl.update(PLAYERS.playersToTeam).set(PLAYERS.playersToTeam.TEAM_ID, null as Int?)
            .where(PLAYERS.playersToTeam.PLAYER_ID.eq(playerId.value))
            .and(PLAYERS.playersToTeam.TEAM_ID.eq(teamId.value)).execute()

        return if (updated > 0) getByTeamWithPlayersId(teamId) else null
    }


    // Helper functions

    private fun fetchTeamRows() = dsl.select(TEAMS.ID, TEAMS.NAME, TEAMS.LOGO_NAME, TEAMS.EVENT_ID).from(TEAMS).fetch()

    private fun getTeamRowById(id: TeamId) =
        dsl.select(TEAMS.ID, TEAMS.NAME, TEAMS.LOGO_NAME, TEAMS.EVENT_ID).from(TEAMS).where(TEAMS.ID.eq(id.value))
            .fetchOne()

    private fun getByTeamWithPlayersId(id: TeamId): TeamWithPlayers? {
        return getTeamRowById(id)?.let(::rowToTeamWithPlayers)
    }

    private fun rowToTeam(row: Record): Team? {
        val teamId = row[TEAMS.ID]?.toTeamId() ?: return null
        val name = row[TEAMS.NAME]?.toTeamName() ?: return null
        val logoName = row[TEAMS.LOGO_NAME]?.toTeamLogoName()
        val eventId = row[TEAMS.EVENT_ID]?.let(::EventId)

        return Team(
            id = teamId,
            name = name,
            logoName = logoName,
            eventId = eventId,
        )
    }

    private fun rowToTeamWithPlayers(row: Record): TeamWithPlayers? {
        val teamId = row[TEAMS.ID]?.toTeamId() ?: return null
        val name = row[TEAMS.NAME]?.toTeamName() ?: return null
        val logoName = row[TEAMS.LOGO_NAME]?.toTeamLogoName()
        val eventId = row[TEAMS.EVENT_ID]?.let(::EventId)
        val players = getPlayersForTeam(teamId)

        return TeamWithPlayers(
            id = teamId, name = name, logoName = logoName, eventId = eventId, players = players
        )
    }

    private fun getPlayersForTeam(teamId: TeamId): List<Player> {
        return dsl.select(PLAYERS.ID, PLAYERS.NAME).from(PLAYERS).where(PLAYERS.playersToTeam.TEAM_ID.eq(teamId.value))
            .fetch().map {
                Player(
                    id = PlayerId(it[PLAYERS.ID]!!), name = PlayerName(it[PLAYERS.NAME]!!)
                )
            }
    }
}