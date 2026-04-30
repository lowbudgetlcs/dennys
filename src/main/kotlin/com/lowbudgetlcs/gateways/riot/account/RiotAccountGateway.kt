package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.account.core.model.RiotAccount
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class RiotAccountGateway(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://americas.api.riotgames.com",
) : IRiotAccountGateway {

    override suspend fun getAccountByPuuid(puuid: Puuid): RiotAccount {
        logger.debug("Fetching account for '$puuid'...")
        val response: HttpResponse =
            client.get("$baseUrl/riot/account/v1/accounts/by-puuid/$puuid") {
                headers {
                    append("X-Riot-Token", apiKey)
                }
            }

        return when (response.status) {
            HttpStatusCode.OK -> {
                logger.debug("Successfully fetched account.")
                response.body<RiotAccountDto>().toRiotAccount()
            }

            HttpStatusCode.BadRequest -> throw IllegalArgumentException("Invalid Riot PUUID")
            HttpStatusCode.NotFound -> throw NoSuchElementException("Riot account not found for PUUID")
            else -> {
                logger.warn("Failed to fetch account.")
                throw RiotApiException("Unexpected Riot API error: ${response.status}")
            }
        }
    }
}
