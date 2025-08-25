package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.Shortcode

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: String): RiotTournament?
    suspend fun getCode(riotTournament: RiotTournament, newShortcode: NewShortcode): Shortcode?
}