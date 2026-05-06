package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.division.core.event.model.RiotTournament
import com.lowbudgetlcs.domain.division.core.event.model.RiotTournamentId
import com.lowbudgetlcs.domain.division.core.event.model.ShortcodeOptions
import com.lowbudgetlcs.domain.division.core.event.model.types.EventName

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: EventName): RiotTournament?

    suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto?
}
