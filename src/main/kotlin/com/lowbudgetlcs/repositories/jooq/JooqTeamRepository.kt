package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.repositories.ITeamRepository
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.PLAYERS
import org.jooq.storage.tables.references.TEAMS

class JooqTeamRepository(
    private val dsl: DSLContext
) : ITeamRepository {

    override fun insert(newTeam: NewTeam): Team? {
        val insertedId = dsl.insertInto(TEAMS)
            .set(TEAMS.NAME, newTeam.name.value)
            .set(TEAMS.LOGO_NAME, newTeam.logoName)
            .returning(TEAMS.ID)
            .fetchOne()
            ?.get(TEAMS.ID)

        return insertedId?.toTeamId()?.let(::getById)
    }

    override fun getAll(): List<Team> {
        return fetchTeamRows().mapNotNull(::rowToTeam)
    }

    override fun getById(id: TeamId): Team? {
        TODO("Not yet implemented")
    }

    override fun updateTeam(id: TeamId, newName: TeamName?, newLogoName: TeamLogoName?): Team? {
        TODO("Not yet implemented")
    }

    override fun insertPlayerToTeam(teamId: TeamId, playerId: PlayerId): TeamWithPlayers? {
        TODO("Not yet implemented")
    }

    override fun removePlayer(teamId: TeamId, playerId: PlayerId): TeamWithPlayers? {
        TODO("Not yet implemented")
    }

    // Helper functions

    private fun fetchTeamRows() = dsl
        .select(TEAMS.ID, TEAMS.NAME, TEAMS.LOGO_NAME)
        .from(TEAMS)
        .fetch()

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
            id = teamId,
            name = name,
            logoName = logoName,
            eventId = eventId,
            players = players
        )
    }

    private fun getPlayersForTeam(teamId: TeamId): List<Player> {
        return dsl.select(PLAYERS.ID, PLAYERS.NAME)
            .from(PLAYERS)
            .where(PLAYERS.playerToTeams.TEAM_ID.eq(teamId.value))
            .fetch()
            .map {
                Player(
                    id = PlayerId(it[PLAYERS.ID]!!),
                    name = PlayerName(it[PLAYERS.NAME]!!)
                )
            }
    }
}