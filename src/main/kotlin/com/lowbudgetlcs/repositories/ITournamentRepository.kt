package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.tournament.NewTournament
import com.lowbudgetlcs.domain.tournament.Tournament

interface ITournamentRepository {
    fun create(tournament: NewTournament): Tournament?
}