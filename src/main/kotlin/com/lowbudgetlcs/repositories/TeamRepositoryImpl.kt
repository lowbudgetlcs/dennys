package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import migrations.Teams

class TeamRepositoryImpl : TeamRepository, Repository<Teams, Int> {
    private val lblcs = LblcsDatabaseBridge().db

    override fun readAll() = lblcs.teamsQueries.selectAll().executeAsList()

    override fun delete(entity: Teams): Teams {
        TODO("Not yet implemented")
    }

    override fun update(entity: Teams) =
        lblcs.teamsQueries.updateTeam(entity.name, entity.logo, entity.captain_id, entity.division_id).executeAsOne()

    override fun create(entity: Teams): Teams {
        TODO("Not yet implemented")
    }

    override fun readById(id: Int) = lblcs.teamsQueries.selectById(id).executeAsOneOrNull()
}