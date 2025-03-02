package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.entities.match.LeagueOfLegendsMatch

interface RiotMatchRepository {
    suspend fun getMatch(matchId: Long): LeagueOfLegendsMatch
}