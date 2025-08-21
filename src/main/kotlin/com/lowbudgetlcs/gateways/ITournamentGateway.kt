package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament

interface ITournamentGateway {
    suspend fun create(tournament: NewTournament): Tournament?
}