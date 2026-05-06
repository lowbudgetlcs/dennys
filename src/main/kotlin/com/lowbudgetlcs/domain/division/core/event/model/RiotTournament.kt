package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.types.EventName

data class RiotTournament(
    val id: RiotTournamentId,
    val name: EventName,
)
