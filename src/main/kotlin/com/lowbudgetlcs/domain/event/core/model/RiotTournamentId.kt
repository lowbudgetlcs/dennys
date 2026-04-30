package com.lowbudgetlcs.domain.event.core.model

@JvmInline
value class RiotTournamentId(
    val value: Int,
)

// Extensions
fun Int.toRiotTournamentId(): RiotTournamentId = RiotTournamentId(this)
