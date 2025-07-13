package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.domain.models.NewTournament
import com.lowbudgetlcs.domain.models.Tournament
import com.lowbudgetlcs.repositories.ITournamentRepository

class RiotTournamentRepository : ITournamentRepository {
    override fun create(tournament: NewTournament): Tournament? = TODO()
}