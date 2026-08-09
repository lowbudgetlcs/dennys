package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.event.models.RiotTournament
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.RiotTournamentId

interface IRiotTournamentGateway {
    suspend fun create(tournamentName: EventName): RiotTournament?

    suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto

    suspend fun getGames(shortcode: Shortcode): List<RiotTournamentGamesV5Dto>
}
