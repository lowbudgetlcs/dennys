package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.division.core.model.RiotTournament
import com.lowbudgetlcs.domain.division.core.model.RiotTournamentId
import com.lowbudgetlcs.domain.division.core.model.ShortcodeOptions
import com.lowbudgetlcs.domain.division.core.model.types.EventName

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: EventName): RiotTournament?

    suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto?
}
