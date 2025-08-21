package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament

class TournamentGateway : ITournamentGateway {
    override fun create(tournament: NewTournament): Tournament {
        // Fetch provider ID from metadata table
        // Considering making this an environment variable...
        // Send request to riot endpoint
        // Return id packaged as Tournament
        TODO("Not yet implemented")
    }
}