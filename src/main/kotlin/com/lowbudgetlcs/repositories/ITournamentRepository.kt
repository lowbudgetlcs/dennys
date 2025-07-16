package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament

interface ITournamentRepository {
    fun create(tournament: NewTournament): Tournament?
}