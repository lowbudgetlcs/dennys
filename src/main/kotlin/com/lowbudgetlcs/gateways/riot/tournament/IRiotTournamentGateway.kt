package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.api.dto.riot.tournament.RiotShortcodeDto
import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: String): RiotTournament?
    suspend fun getCode(riotTournamentId: RiotTournamentId, newShortcode: NewShortcode): RiotShortcodeDto?
}