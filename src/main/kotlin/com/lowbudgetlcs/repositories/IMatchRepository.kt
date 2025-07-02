package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch

interface IMatchRepository {
    /* Fetches a LeagueOfLegendsMatch from the Riot API, returns null if none found */
    suspend fun getMatch(gameId: Long): LeagueOfLegendsMatch?
}