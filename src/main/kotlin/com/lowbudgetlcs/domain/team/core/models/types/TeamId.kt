package com.lowbudgetlcs.domain.team.core.models.types

@JvmInline
value class TeamId(
    val value: Int,
)

// Extensions
fun Int.toTeamId(): TeamId = TeamId(this)
