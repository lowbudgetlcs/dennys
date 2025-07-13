package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.NewTournament
import com.lowbudgetlcs.domain.models.Tournament
import com.lowbudgetlcs.domain.models.TournamentId
import com.lowbudgetlcs.repositories.ITournamentRepository

class InMemoryTournamentRepository : ITournamentRepository {
    val tournaments: MutableList<Tournament> = mutableListOf()
    fun clear() {
        tournaments.clear()
    }

    override fun create(tournament: NewTournament): Tournament? {
        val id = TournamentId(tournaments.size)
        val t = Tournament(
            id = id,
            name = tournament.name
        )
        tournaments.add(id.value, t)
        return tournaments[id.value]
    }
}