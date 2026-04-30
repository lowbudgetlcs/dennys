package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.event.core.model.RiotTournament
import com.lowbudgetlcs.domain.event.core.model.RiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.ShortcodeOptions
import com.lowbudgetlcs.domain.event.core.model.types.EventName

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: EventName): RiotTournament?

    suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto?
}
