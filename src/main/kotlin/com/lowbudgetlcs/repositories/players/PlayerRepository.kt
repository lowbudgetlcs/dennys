package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Player
import com.lowbudgetlcs.models.PlayerId
import com.lowbudgetlcs.repositories.Repository

interface PlayerRepository : Repository<Player, PlayerId> {
    fun readByPuuid(puuid: String): Player?
    fun createPlayerData(
        player: Player,
        game: Game,
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
    ): Player
}