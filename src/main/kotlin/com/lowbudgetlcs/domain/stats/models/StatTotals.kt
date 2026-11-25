package com.lowbudgetlcs.domain.stats.models

data class StatTotals(
    val kills: Long,
    val deaths: Long,
    val assists: Long,
    val cs: Long,
    val goldEarned: Long,
    val damageToChampions: Long,
    val damageTaken: Long,
    val visionScore: Long,
    val wardsPlaced: Long,
    val wardsKilled: Long,
    val controlWardsBought: Long,
    val voidGrubs: Long,
    val gameDurationSeconds: Long,
)
