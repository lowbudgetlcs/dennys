package com.lowbudgetlcs.models.match

import com.lowbudgetlcs.models.match.runes.MatchParticipantPerks
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchParticipant(
    @SerialName("participantId") val id: Int,
    @SerialName("puuid") val playerUniqueUserId: String,
    @SerialName("profileIcon") val profileIcon: Long,
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
    @SerialName("goldSpent") val goldSpent: Int,
    @SerialName("totalDamageDealtToChampions") val totalDamageToChampions: Long,
    @SerialName("totalHealsOnTeammates") val totalHealsOnTeammates: Long,
    @SerialName("totalDamageShieldedOnTeammates") val totalDamageShieldedOnTeammates: Long,
    @SerialName("totalDamageTaken") val totalDamageTaken: Long,
    @SerialName("damageSelfMitigated") val damageSelfMitigated: Long,
    @SerialName("damageDealtToTurrets") val damageDealtToTurrets: Long,
    @SerialName("longestTimeSpentLiving") val longestTimeSpentLiving: Long,
    @SerialName("largestCriticalStrike") val largestCriticalStrike: Int,

    // **Multi-kill Statistics**
    @SerialName("largestMultiKill") val largestMultiKill: Short,
    @SerialName("largestKillingSpree") val largestKillingSpree: Short,
    @SerialName("doubleKills") val doubleKills: Short,
    @SerialName("tripleKills") val tripleKills: Short,
    @SerialName("quadraKills") val quadraKills: Short,
    @SerialName("pentaKills") val pentaKills: Short,

    // **First Blood**
    @SerialName("firstBloodAssist") val firstBloodAssist: Boolean,
    @SerialName("firstBloodKill") val firstBloodKill: Boolean,

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
    @SerialName("win") val win: Boolean,

    // **Player Interactions - Pings**
    @SerialName("allInPings") val allInPings: Short,
    @SerialName("assistMePings") val assistMePings: Short,
    @SerialName("commandPings") val commandPings: Short,
    @SerialName("enemyMissingPings") val enemyMissingPings: Short,
    @SerialName("enemyVisionPings") val enemyVisionPings: Short,
    @SerialName("holdPings") val holdPings: Short,
    @SerialName("getBackPings") val getBackPings: Short,
    @SerialName("onMyWayPings") val onMyWayPings: Short,
    @SerialName("needVisionPings") val needVisionPings: Short,
    @SerialName("pushPings") val pushPings: Short,

    // **Objective Stats**
    @SerialName("baronKills") val baronKills: Short,
    @SerialName("dragonKills") val dragonKills: Short,
    @SerialName("firstTowerKill") val firstTowerKill: Boolean,
    @SerialName("firstTowerAssist") val firstTowerAssist: Boolean,
    @SerialName("turretKills") val turretKills: Short,
    @SerialName("turretTakedowns") val turretTakedowns: Short,
    @SerialName("inhibitorKills") val inhibitorKills: Short,
    @SerialName("inhibitorTakedowns") val inhibitorTakedowns: Short,
    @SerialName("inhibitorsLost") val inhibitorsLost: Short,
    @SerialName("objectivesStolen") val objectivesStolen: Short,


    // **Vision Stats**
    @SerialName("visionScore") val visionScore: Long,
    @SerialName("pinkWardsPlaced") val pinkWardsPlaced: Short,
    @SerialName("wardsKilled") val wardsKilled: Short,
    @SerialName("wardsPlaced") val wardsPlaced: Short
)
