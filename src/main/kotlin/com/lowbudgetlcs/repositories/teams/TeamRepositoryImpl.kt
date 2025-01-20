package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.repositories.Criteria
import migrations.Teams
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType
import no.stelar7.api.r4j.pojo.lol.match.v5.ObjectiveStats

class TeamRepositoryImpl : TeamRepository {
    private val lblcs = LblcsDatabaseBridge().db
    override fun createTeamData(
        team: Team, game: Game, side: TeamType, win: Boolean, gold: Int, objectives: Map<String, ObjectiveStats>
    ): Team = TODO("Not yet implemented")

    override fun readAll(): List<Team> = lblcs.teamsQueries.selectAll().executeAsList().map { transform(it) }

    override fun delete(entity: Team): Team {
        TODO("Not yet implemented")
    }

    override fun update(entity: Team): Team =
        lblcs.teamsQueries.updateTeam(entity.name, entity.logo, entity.captain?.id, entity.division?.id)
            .executeAsOneOrNull().let { if (it != null) transform(it) else entity }

    override fun create(entity: Team): Team {
        TODO("Not yet implemented")
    }

    override fun readById(id: TeamId): Team? =
        lblcs.teamsQueries.selectById(id.id).executeAsOneOrNull()?.let { transform(it) }

    override fun readByCriteria(criteria: Criteria<Team>): List<Team> {
        TODO("Not yet implemented")
    }

    private fun readTeamData(): List<TeamGameData> = TODO("Not yet implemented")

    private fun transform(entity: Teams): Team {
        val teamData by lazy {
            readTeamData()
        }
        return Team(
            TeamId(entity.id),
            entity.name,
            entity.logo,
            entity.captain_id?.let { PlayerId(it) },
            entity.division_id?.let {
                DivisionId(
                    it
                )
            },
            teamData
        )
    }
}
