package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.account.models.RiotAccount
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.gateways.riot.RiotApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotAccountGateway(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://americas.api.riotgames.com",
) : IRiotAccountGateway {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun getAccountByPuuid(puuid: Puuid): RiotAccount {
        logger.debug("Fetching account for '${puuid.value}'...")
        val response: HttpResponse =
            client.get(baseUrl) {
                // Appended as a path segment rather than interpolated: interpolating the Puuid
                // wrapper sent its toString(), so Riot received 'Puuid(value=...)' and answered
                // 400, which surfaced to clients as "Invalid Riot PUUID" for valid accounts.
                url {
                    appendPathSegments(
                        "riot",
                        "account",
                        "v1",
                        "accounts",
                        "by-puuid",
                        puuid.value,
                        encodeSlash = true,
                    )
                }
                headers {
                    append("X-Riot-Token", apiKey)
                }
            }

        return when (response.status) {
            HttpStatusCode.OK -> {
                logger.debug("Successfully fetched account.")
                response.body<RiotAccountDto>().toRiotAccount()
            }

            HttpStatusCode.BadRequest -> {
                // Puuid validates its own format, so Riot rejecting the value points at the
                // request we built rather than at the caller. Name the URL to keep that visible.
                logger.warn("Riot rejected the PUUID in '${response.request.url}' as malformed.")
                throw IllegalArgumentException("Invalid Riot PUUID")
            }

            HttpStatusCode.NotFound -> throw NoSuchElementException("Riot account not found for PUUID")
            else -> {
                logger.warn("Failed to fetch account from '${response.request.url}': ${response.status}")
                throw RiotApiException("Unexpected Riot API error: ${response.status}", response.status.value)
            }
        }
    }
}
