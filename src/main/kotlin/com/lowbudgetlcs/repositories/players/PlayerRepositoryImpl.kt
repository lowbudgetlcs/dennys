package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.repositories.Repository
import migrations.Players


class PlayerRepositoryImpl : PlayerRepository, Repository<Players, Int> {

    private val lblcs = LblcsDatabaseBridge().db

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
    override fun createPerformance(puuid: String, gameId: Int) =
        lblcs.playerPerformancesQueries.createPerformance(puuid, gameId).executeAsOneOrNull()

    override fun createGameData(
        performanceId: Int,
        kills: Int,
        deaths: Int,
        assists: Int,
        championLevel: Int,
        goldEarned: Long,
        visionScore: Long,
        totalDamageToChampions: Long,
        totalHealsOnTeammates: Long,
        totalDamageShieldedOnTeammates: Long,
        totalDamageTaken: Long,
        damageSelfMitigated: Long,
        damageDealtToTurrets: Long,
        longestTimeSpentLiving: Long,
        doubleKills: Short,
        tripleKills: Short,
        quadraKills: Short,
        pentaKills: Short,
        cs: Int,
        championName: String,
        item0: Int,
        item1: Int,
        item2: Int,
        item3: Int,
        item4: Int,
        item5: Int,
        item6: Int,
        keystone: Int,
        secondaryKeystone: Int,
        summoner1: Int,
        summoner2: Int
    ): Boolean = lblcs.playerDataQueries.createPlayerData(
        performanceId,
        kills,
        deaths,
        assists,
        championLevel,
        goldEarned,
        visionScore,
        totalDamageToChampions,
        totalHealsOnTeammates,
        totalDamageShieldedOnTeammates,
        totalDamageTaken,
        damageSelfMitigated,
        damageDealtToTurrets,
        longestTimeSpentLiving,
        doubleKills,
        tripleKills,
        quadraKills,
        pentaKills,
        cs,
        championName,
        item0,
        item1,
        item2,
        item3,
        item4,
        item5,
        item6,
        keystone,
        secondaryKeystone,
        summoner1,
        summoner2
    ).executeAsOneOrNull() != null
}