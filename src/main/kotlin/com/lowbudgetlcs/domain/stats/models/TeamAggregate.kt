package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.team.models.types.TeamId

data class TeamAggregate(
    val teamId: TeamId,
    val teamName: String?,
    val games: Int,
    val wins: Int,
    val firstBloods: Long,
    val firstTowers: Long,
    val firstDragons: Long,
    val firstBarons: Long,
    val kills: Long,
    val towerKills: Long,
    val inhibitorKills: Long,
    val dragonKills: Long,
    val baronKills: Long,
    val riftHeraldKills: Long,
    val hordeKills: Long,
    val gameDurationSeconds: Long,
)
