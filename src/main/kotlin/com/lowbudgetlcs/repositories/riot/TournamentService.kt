package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.models.tournament.CodesRequest

interface TournamentService {

    suspend fun generateTournamentCode(
        tournamentId: Int,
        count: Int = 1,
        codesRequest: CodesRequest
    ): List<String>

}