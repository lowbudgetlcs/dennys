package com.lowbudgetlcs.domain.event.models

@JvmInline
value class RiotTournamentId(
    val value: Int,
)

// Extensions
fun Int.toRiotTournamentId(): RiotTournamentId = RiotTournamentId(this)
