package com.lowbudgetlcs.models

import com.lowbudgetlcs.repositories.players.PlayerRepositoryImpl
import kotlinx.serialization.Serializable
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant

@Serializable
data class PlayerId(val id: Int)

@Serializable
data class Player(
    val id: PlayerId,
    val summonerName: String,
    val puuid: String,
    val team: TeamId?,
    val gameData: List<PlayerGameData>
)

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

fun fetchTeamId(participants: List<MatchParticipant>): TeamId? {
    val playersR = PlayerRepositoryImpl()
    for (participant in participants) {
        playersR.readByPuuid(participant.puuid)?.let { player ->
            if (player.team != null) return player.team
        }
    }
    return null
}