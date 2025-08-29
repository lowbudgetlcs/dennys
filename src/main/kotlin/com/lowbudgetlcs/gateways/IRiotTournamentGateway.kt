package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import com.lowbudgetlcs.api.dto.riot.tournament.RiotShortcodeDto

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: String): RiotTournament?
    suspend fun getCode(riotTournamentId: RiotTournamentId, newShortcode: NewShortcode): RiotShortcodeDto?
}