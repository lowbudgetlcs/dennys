package com.lowbudgetlcs.repositories.teams

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.entities.*
import com.lowbudgetlcs.repositories.Criteria
import migrations.Team_game_data
import migrations.Teams

class TeamRepositoryImpl : ITeamRepository {
    private val lblcs = LblcsDatabaseBridge().db

    override fun create(entity: Team): Team {
        TODO("Not yet implemented")
    }

    override fun createTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team {
        lblcs.transaction {
            createTeamGameData(createTeamPerformance(team, game), data)
        }
        val teamData by lazy {
            readTeamData(team.id)
        }
        return team.copy(teamData = teamData)
    }

    override fun readAll(): List<Team> = lblcs.teamsQueries.readAll().executeAsList().map { it.toTeam() }

    override fun readById(id: TeamId) = lblcs.teamsQueries.readById(id.id).executeAsOneOrNull()?.toTeam()

    override fun readByCriteria(criteria: Criteria<Team>): List<Team> = criteria.meetCriteria(readAll())

    override fun update(entity: Team): Team =
        lblcs.teamsQueries.updateTeam(entity.name, entity.logo, entity.captain?.id, entity.division?.id)
            .executeAsOneOrNull().let { it?.toTeam() ?: entity }

    override fun delete(entity: Team): Team {
        TODO("Not yet implemented")
    }

    private fun createTeamPerformance(team: Team, game: Game) =
        TeamPerformanceId(lblcs.teamsQueries.createPerformance(team.id.id, game.id.id).executeAsOne())

    private fun createTeamGameData(performance: TeamPerformanceId, data: TeamGameData) {
        lblcs.teamsQueries.createGameData(
            performance.id,
            data.win,
            data.side.name,
            data.gold,
            data.gameLength,
            data.kills.kills,
            data.barons.kills,
            data.dragons.kills,
            data.grubs.kills,
            data.heralds.kills,
            data.towers.kills,
            data.inhibitors.kills,
            data.kills.first,
            data.barons.first,
            data.dragons.first,
            data.grubs.first,
            data.heralds.first,
            data.towers.first,
            data.inhibitors.first
        ).executeAsOne()
    }

    private fun readTeamData(teamId: TeamId): List<TeamGameData> =
        lblcs.teamsQueries.readTeamDataById(teamId.id).executeAsList().let { data ->
            data.map {
                it.toTeamGameData()
            }
        }

    private fun Teams.toTeam(): Team {
        val teamData by lazy {
            readTeamData(TeamId(id))
        }
        return Team(
            TeamId(id), name, logo, captain_id?.let { PlayerId(it) }, division_id?.let {
                DivisionId(
                    it
                )
            }, teamData
        )
    }

    private fun Team_game_data.toTeamGameData(): TeamGameData = TeamGameData(
        win = this.win,
        side = if (side == "BLUE") RiftSide.BLUE else RiftSide.RED,
        gold = this.gold,
        gameLength = this.game_length,
        kills = Objective(kills = this.kills, first = this.first_blood),
        barons = Objective(kills = this.barons, first = this.first_baron),
        grubs = Objective(kills = this.grubs, first = this.first_grub),
        dragons = Objective(kills = this.dragons, first = this.first_dragon),
        heralds = Objective(kills = this.heralds, first = this.first_herald),
        towers = Objective(kills = this.towers, first = this.first_tower),
        inhibitors = Objective(kills = this.inhibitors, first = this.first_inhibitor)
    )
}
