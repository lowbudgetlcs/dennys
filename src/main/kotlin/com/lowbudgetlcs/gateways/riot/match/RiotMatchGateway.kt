package com.lowbudgetlcs.gateways.riot.match

import com.lowbudgetlcs.domain.series.game.models.types.RiotMatchId
import com.lowbudgetlcs.gateways.riot.RiotApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotMatchGateway(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://americas.api.riotgames.com",
) : IRiotMatchGateway {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun getMatch(matchId: RiotMatchId): JsonObject = fetch(matchId.value)

    override suspend fun getTimeline(matchId: RiotMatchId): JsonObject = fetch(matchId.value, "timeline")

    private suspend fun fetch(vararg segments: String): JsonObject {
        val response: HttpResponse =
            client.get(baseUrl) {
                url { appendPathSegments("lol", "match", "v5", "matches", *segments, encodeSlash = true) }
                headers {
                    append("X-Riot-Token", apiKey)
                }
            }

        return when (response.status) {
            HttpStatusCode.OK -> response.body<JsonObject>()
            HttpStatusCode.NotFound -> throw NoSuchElementException("Riot has no data at '${response.request.url}'")
            else -> {
                logger.warn("Failed to fetch '${response.request.url}': ${response.status}")
                throw RiotApiException("Unexpected Riot API error: ${response.status}", response.status.value)
            }
        }
    }
}
