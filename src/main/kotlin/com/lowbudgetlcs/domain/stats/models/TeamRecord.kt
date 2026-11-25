package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.team.models.types.TeamId

data class TeamRecord(
    val teamId: TeamId,
    val teamName: String?,
    val seriesWon: Int,
    val seriesLost: Int,
    val gamesWon: Int,
    val gamesLost: Int,
)
