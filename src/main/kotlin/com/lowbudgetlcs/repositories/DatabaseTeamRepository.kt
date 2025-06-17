package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.models.*
import migrations.Team_game_data
import migrations.Teams

class DatabaseTeamRepository(private val lblcs: Database) : ITeamRepository {

    /**
     * Returns a [Team] derived from [Teams]. [Team.teamData] is lazy-loaded.
     */
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

    /**
     * Returns a [TeamGameData] derived from [Team_game_data].
     */
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

    override fun saveTeamData(
        team: Team, game: Game, data: TeamGameData
    ): Team? {
        try {
            lblcs.transaction {
                saveTeamGameData(saveTeamPerformance(team, game), data)
            }
        } catch (e: Throwable) {
            return null
        }
        val teamData by lazy {
            readTeamData(team.id)
        }
        return team.copy(teamData = teamData)
    }

    override fun getAll(): List<Team> = lblcs.teamsQueries.readAll().executeAsList().map { it.toTeam() }

    override fun get(id: TeamId): Team? = lblcs.teamsQueries.readById(id.id).executeAsOneOrNull()?.toTeam()

    override fun update(entity: Team): Team? =
        lblcs.teamsQueries.updateTeam(entity.name, entity.logo, entity.captain?.id, entity.division?.id)
            .executeAsOneOrNull()?.toTeam()

    /**
     * Saves team game data derived from [team] and [game] to storage and returns
     * its [TeamPerformanceId]. Throws an exception if insertion fails.
     * @throws NullPointerException
     * @throws IllegalStateException
     */
    private fun saveTeamPerformance(team: Team, game: Game): TeamPerformanceId =
        TeamPerformanceId(lblcs.teamsQueries.createPerformance(team.id.id, game.id.id).executeAsOne())

    /**
     * Saves [performance] and [data] to storage and returns an [Int]. Throws
     * an exception if insertion fails.
     * @throws NullPointerException
     * @throws IllegalStateException
     */
    private fun saveTeamGameData(performance: TeamPerformanceId, data: TeamGameData) {
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
        ).executeAsOneOrNull()
    }

    /**
     * Returns a list of [TeamGameData] belonging to the team with id [teamId].
     */
    private fun readTeamData(teamId: TeamId): List<TeamGameData> =
        lblcs.teamsQueries.readTeamDataById(teamId.id).executeAsList().let { data ->
            data.map {
                it.toTeamGameData()
            }
        }
}
