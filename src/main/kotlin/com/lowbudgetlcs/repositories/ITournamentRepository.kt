package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.NewTournament
import com.lowbudgetlcs.domain.models.Tournament

interface ITournamentRepository {
    fun create(tournament: NewTournament): Tournament?
}