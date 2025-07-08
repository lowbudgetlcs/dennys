package com.lowbudgetlcs.domain.models

import java.time.OffsetDateTime

data class Player(
    val id: Int, val name: String, val accounts: List<RiotAccount>, val teamId: Int?, val eventId: Int?
)

data class PlayerAuditLog(
    val id: Int,
    val playerId: Int,
    val createdAt: OffsetDateTime,
    val action: String,
    val message: String,
    val origin: String
)

data class PlayerGameData(
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val championLevel: Int,
    val goldEarned: Long,
    val visionScore: Long,
    val totalDamageToChampions: Long,
    val totalHealsOnTeammates: Long,
    val totalDamageShieldedOnTeammates: Long,
    val totalDamageTaken: Long,
    val damageSelfMitigated: Long,
    val damageDealtToTurrets: Long,
    val longestTimeSpentLiving: Long,
    val doubleKills: Short,
    val tripleKills: Short,
    val quadraKills: Short,
    val pentaKills: Short,
    val cs: Int,
    val championName: String,
    val item0: Int?,
    val item1: Int?,
    val item2: Int?,
    val item3: Int?,
    val item4: Int?,
    val item5: Int?,
    val item6: Int?,
    val keystone: Int,
    val secondaryKeystone: Int,
    val summoner1: Int,
    val summoner2: Int
)