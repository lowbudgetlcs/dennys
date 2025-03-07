package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch

interface IMatchRepository {
    suspend fun getMatch(gameId: Long): LeagueOfLegendsMatch?
}