package com.lowbudgetlcs.domain.division.core.event.model

@JvmInline
value class RiotTournamentId(
    val value: Int,
)

// Extensions
fun Int.toRiotTournamentId(): RiotTournamentId = RiotTournamentId(this)
