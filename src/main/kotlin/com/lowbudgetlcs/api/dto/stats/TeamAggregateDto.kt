package com.lowbudgetlcs.api.dto.stats

import kotlinx.serialization.Serializable

@Serializable
data class TeamAggregateDto(
    val teamId: Int,
    val teamName: String?,
    val games: Int,
    val wins: Int,
    val firstBloods: Long,
    val firstBloodsPerGame: Double,
    val firstTowers: Long,
    val firstTowersPerGame: Double,
    val firstDragons: Long,
    val firstDragonsPerGame: Double,
    val firstBarons: Long,
    val firstBaronsPerGame: Double,
    val kills: Long,
    val killsPerGame: Double,
    val towerKills: Long,
    val towerKillsPerGame: Double,
    val inhibitorKills: Long,
    val inhibitorKillsPerGame: Double,
    val dragonKills: Long,
    val dragonKillsPerGame: Double,
    val baronKills: Long,
    val baronKillsPerGame: Double,
    val riftHeraldKills: Long,
    val riftHeraldKillsPerGame: Double,
    val hordeKills: Long,
    val hordeKillsPerGame: Double,
    val gameDurationSeconds: Long,
    val gameDurationSecondsPerGame: Double,
)
