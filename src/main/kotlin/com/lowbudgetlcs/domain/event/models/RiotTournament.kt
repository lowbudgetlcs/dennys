package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.RiotTournamentId

data class RiotTournament(
    val id: RiotTournamentId,
    val name: EventName,
)
