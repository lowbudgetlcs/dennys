package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.division.core.model.types.EventName

data class RiotTournament(
    val id: RiotTournamentId,
    val name: EventName,
)
