package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.http.RateLimiter
import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun HttpStatusCode.failed(): Boolean = this.value > 299 || this.value < 200

class MatchRepositoryRiot(
    private val apiClient: RiotApiClient, private val rateLimiter: RateLimiter
) : IMatchRepository {

    private val logger: Logger = LoggerFactory.getLogger(MatchRepositoryRiot::class.java)

    override suspend fun getMatch(gameId: Long): LeagueOfLegendsMatch? {
        val uri = "https://americas.api.riotgames.com/lol/match/v5/matches/NA1_$gameId"

        return try {
            logger.info("🔍 Fetching match data for game ID: $gameId")
            val response: HttpResponse = apiClient.get(uri)
            if (response.status.failed()) return null.also {
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