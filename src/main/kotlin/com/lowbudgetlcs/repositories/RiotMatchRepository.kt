package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch
import com.lowbudgetlcs.http.RateLimiter
import io.ktor.client.call.*
import io.ktor.client.statement.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotMatchRepository(
    private val apiClient: RiotApiClient,
    private val rateLimiter: RateLimiter
) : IMatchRepository {

    private val logger: Logger = LoggerFactory.getLogger(RiotMatchRepository::class.java)

    override suspend fun getMatch(gameId: Long): LeagueOfLegendsMatch? {
        val uri = "https://americas.api.riotgames.com/lol/match/v5/matches/NA1_$gameId"

        return try {
            rateLimiter.acquire(uri)
            logger.info("🔍 Fetching match data for game ID: $gameId")
            val response: HttpResponse = apiClient.get(uri)
            rateLimiter.updateLimits(response, uri)

            if (!response.status.value.toString().startsWith("2")) return null.also {
                logger.error("❌ API responded with status: ${response.status}")
            }

            response.body<LeagueOfLegendsMatch>().also {
                logger.info("✅ Successfully retrieved match data for game ID: $gameId")
            }
        } catch (e: Throwable) {
            null.also {
                logger.error("❌ Error while retrieving match '$gameId'", e)
            }
        }
    }
}