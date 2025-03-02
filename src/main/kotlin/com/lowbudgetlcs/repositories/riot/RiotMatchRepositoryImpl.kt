package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch
import com.lowbudgetlcs.util.RateLimiter
import io.ktor.client.call.*
import io.ktor.client.statement.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotMatchRepositoryImpl(
    private val apiClient: RiotApiClient,
    private val rateLimiter: RateLimiter
) : RiotMatchRepository {

    private val logger: Logger = LoggerFactory.getLogger(RiotMatchRepositoryImpl::class.java)

    override suspend fun getMatch(matchId: Long): LeagueOfLegendsMatch? {
        val uri = "https://americas.api.riotgames.com/lol/match/v5/matches/NA1_$matchId"

        return try {
            rateLimiter.acquire(uri)

            logger.info("🔍 Fetching match data for game ID: $matchId")
            val response: HttpResponse = apiClient.get(uri)

            rateLimiter.updateLimits(response, uri)

            if (!response.status.value.toString().startsWith("2")) {
                logger.error("❌ API responded with status: ${response.status}")
                return null
            }

            val match: LeagueOfLegendsMatch = response.body()
            logger.info("✅ Successfully retrieved match data for game ID: $matchId")
            match
        } catch (e: Throwable) {
            logger.error("❌ Error while retrieving match '$matchId'", e)
            null
        }
    }
}