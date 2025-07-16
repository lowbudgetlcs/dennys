package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.domain.models.tournament.toTournament
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.repositories.ITournamentRepository

class InMemoryTournamentRepository : ITournamentRepository {
    val tournaments: MutableList<Tournament> = mutableListOf()
    fun clear() {
        tournaments.clear()
    }

    override fun create(tournament: NewTournament): Tournament? {
        val id = tournaments.size.toTournamentId()
        val t = tournament.toTournament(id)
        tournaments.add(id.value, t)
        return tournaments[id.value]
    }
}