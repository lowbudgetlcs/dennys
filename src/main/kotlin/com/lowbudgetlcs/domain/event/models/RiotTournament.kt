package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.EventName

data class RiotTournament(
    val id: RiotTournamentId,
    val name: EventName,
)
