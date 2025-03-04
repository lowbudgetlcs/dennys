package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable

/**
 * Represents a player. [summonerName] is not safe to cache as it can
 * change with no warning. [puuid] is encrypted on a per-riot-token basis
 * but is immutable.
 */
@Serializable
data class Player (
    override val id: PlayerId, val summonerName: String, val puuid: String, val team: TeamId?, val gameData: List<PlayerGameData>
): Entity<PlayerId>

/**
 * ID type for [Player]s.
 */
@Serializable
data class PlayerId(val id: Int)

/**
 * Represents in-game stats owned by a [Player].
 */
@Serializable
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