package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.domain.tournament.NewTournament
import com.lowbudgetlcs.domain.tournament.Tournament
import com.lowbudgetlcs.repositories.ITournamentRepository

class RiotTournamentRepository : ITournamentRepository {
    override fun create(tournament: NewTournament): Tournament? = TODO()
}