package com.lowbudgetlcs.repositories.players

import migrations.Players

interface PlayerRepository {
    fun updateSummonerNameByPuuid(puuid: String, summonerName: String): Players?
    fun readByPuuid(puuid: String): Players?
    fun createPerformance(puuid: String, gameId: Int): Int?
    fun createGameData(
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
    ): Boolean
}