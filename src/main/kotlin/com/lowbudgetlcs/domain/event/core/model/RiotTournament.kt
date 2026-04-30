package com.lowbudgetlcs.domain.event.core.model

import com.lowbudgetlcs.domain.event.core.model.types.EventName

data class RiotTournament(
    val id: RiotTournamentId,
    val name: EventName,
)
