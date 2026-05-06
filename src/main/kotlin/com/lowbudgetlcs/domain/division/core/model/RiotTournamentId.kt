package com.lowbudgetlcs.domain.division.core.model

@JvmInline
value class RiotTournamentId(
    val value: Int,
)

// Extensions
fun Int.toRiotTournamentId(): RiotTournamentId = RiotTournamentId(this)
