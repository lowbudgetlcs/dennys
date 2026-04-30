package com.lowbudgetlcs.domain.team.core.model.types

@JvmInline
value class TeamId(
    val value: Int,
)

// Extensions
fun Int.toTeamId(): TeamId = TeamId(this)
