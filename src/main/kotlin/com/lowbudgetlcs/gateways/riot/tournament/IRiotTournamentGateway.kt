package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.models.events.RiotTournament
import com.lowbudgetlcs.domain.models.events.RiotTournamentId
import com.lowbudgetlcs.domain.models.events.ShortcodeOptions

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: String): RiotTournament?

    suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto?
}
