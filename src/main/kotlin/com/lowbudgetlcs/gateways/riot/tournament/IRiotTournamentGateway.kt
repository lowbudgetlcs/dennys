package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.event.models.RiotTournament
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.EventName
import com.lowbudgetlcs.domain.event.models.RiotTournamentId

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: EventName): RiotTournament?

    suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto?
}
