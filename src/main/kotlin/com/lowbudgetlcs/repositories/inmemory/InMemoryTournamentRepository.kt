package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.tournament.NewTournament
import com.lowbudgetlcs.domain.tournament.Tournament
import com.lowbudgetlcs.repositories.ITournamentRepository

class InMemoryTournamentRepository : ITournamentRepository {
    val tournaments: MutableList<Tournament> = mutableListOf()
    override fun create(tournament: NewTournament): Tournament? {
        val id = tournaments.size
        val t = Tournament(
            id = id,
            metadata = tournament.metadata,
            pickType = tournament.pickType,
            mapType = tournament.mapType
        )
        tournaments.add(id, t)
        return tournaments[id]
    }
}