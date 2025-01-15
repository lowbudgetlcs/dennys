package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.repositories.Repository
import migrations.Players


class PlayerRepositoryImpl : PlayerRepository, Repository<Players, Int> {

    private val lblcs = LblcsDatabaseBridge.db

    override fun readAll() = lblcs.playersQueries.selectAll().executeAsList()

    override fun readById(id: Int): Players? {
        TODO("Not yet implemented")
    }

    override fun create(entity: Players): Players {
        TODO("Not yet implemented")
    }

    override fun update(entity: Players): Players {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Players): Players {
        TODO("Not yet implemented")
    }

    override fun updateSummonerNameByPuuid(puuid: String, summonerName: String) =
        lblcs.playersQueries.updateSummonerNameByPuuid(summoner_name = summonerName, puuid = puuid).executeAsOneOrNull()

    override fun readByPuuid(puuid: String): Players? = lblcs.playersQueries.selectByPuuid(puuid).executeAsOneOrNull()
}