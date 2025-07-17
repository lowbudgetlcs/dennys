package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.domain.models.tournament.toTournamentId

class MockTournamentGateway : ITournamentGateway {
    override fun create(tournament: NewTournament): Tournament {
        return Tournament((10000..200000).random().toTournamentId(), tournament.name)
    }
}