package com.lowbudgetlcs.api.dto.stats

import kotlinx.serialization.Serializable

@Serializable
data class ChampionAggregateDto(
    val championId: Int,
    val championName: String?,
    val picks: Int,
    val wins: Int,
    val bans: Int,
    val kills: Long,
    val killsPerGame: Double,
    val deaths: Long,
    val deathsPerGame: Double,
    val assists: Long,
    val assistsPerGame: Double,
    val cs: Long,
    val csPerGame: Double,
    val goldEarned: Long,
    val goldEarnedPerGame: Double,
    val damageToChampions: Long,
    val damageToChampionsPerGame: Double,
    val damageTaken: Long,
    val damageTakenPerGame: Double,
    val visionScore: Long,
    val visionScorePerGame: Double,
    val wardsPlaced: Long,
    val wardsPlacedPerGame: Double,
    val wardsKilled: Long,
    val wardsKilledPerGame: Double,
    val controlWardsBought: Long,
    val controlWardsBoughtPerGame: Double,
    val voidGrubs: Long,
    val voidGrubsPerGame: Double,
    val gameDurationSeconds: Long,
    val gameDurationSecondsPerGame: Double,
)
