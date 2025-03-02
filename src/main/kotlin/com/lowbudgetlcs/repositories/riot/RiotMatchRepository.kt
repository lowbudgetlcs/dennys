package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch

interface RiotMatchRepository {
    suspend fun getMatch(matchId: Long): LeagueOfLegendsMatch?
}