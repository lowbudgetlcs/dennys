package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchParticipant(
    @SerialName("participantId") val id: Int,
    @SerialName("puuid") val playerUniqueUserId: String,
    @SerialName("riotIdGameName") val riotGameName: String?,
    @SerialName("riotIdTagline") val riotTagline: String?,
    @SerialName("teamId") val teamId: Int,
    @SerialName("championId") val championId: Int,
    @SerialName("championName") val championName: String,
    @SerialName("role") val role: String?, // Can be an Enum if needed
    @SerialName("individualPosition") val position: String?,

    // **Performance Metrics**
    @SerialName("kills") val kills: Int,
    @SerialName("deaths") val deaths: Int,
    @SerialName("assists") val assists: Int,
    @SerialName("champLevel") val championLevel: Int,
    @SerialName("goldEarned") val goldEarned: Int,
    @SerialName("visionScore") val visionScore: Long,
    @SerialName("totalDamageDealtToChampions") val totalDamageToChampions: Long,
    @SerialName("totalHealsOnTeammates") val totalHealsOnTeammates: Long,
    @SerialName("totalDamageShieldedOnTeammates") val totalDamageShieldedOnTeammates: Long,
    @SerialName("totalDamageTaken") val totalDamageTaken: Long,
    @SerialName("damageSelfMitigated") val damageSelfMitigated: Long,
    @SerialName("damageDealtToTurrets") val damageDealtToTurrets: Long,
    @SerialName("longestTimeSpentLiving") val longestTimeSpentLiving: Long,

    // **Multi-kill Statistics**
    @SerialName("doubleKills") val doubleKills: Short,
    @SerialName("tripleKills") val tripleKills: Short,
    @SerialName("quadraKills") val quadraKills: Short,
    @SerialName("pentaKills") val pentaKills: Short,

    // **Creep Score**
    @SerialName("totalMinionsKilled") val totalMinionsKilled: Int,
    @SerialName("neutralMinionsKilled") val neutralMinionsKilled: Int,

    // **Items**
    @SerialName("item0") val item0: Int,
    @SerialName("item1") val item1: Int,
    @SerialName("item2") val item2: Int,
    @SerialName("item3") val item3: Int,
    @SerialName("item4") val item4: Int,
    @SerialName("item5") val item5: Int,
    @SerialName("item6") val item6: Int,

    // **Perks**
    @SerialName("perks") val perks: MatchParticipantPerks,

    // **Summoner Spells**
    @SerialName("summoner1Id") val summoner1Id: Int,
    @SerialName("summoner2Id") val summoner2Id: Int,

    // **Win Status**
    @SerialName("win") val win: Boolean
)
