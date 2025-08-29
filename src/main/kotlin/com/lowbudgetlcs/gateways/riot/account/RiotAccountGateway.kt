package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.api.dto.riot.account.RiotAccountDto
import com.lowbudgetlcs.domain.models.riot.RiotApiException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotAccountGateway(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://americas.api.riotgames.com"
) : IRiotAccountGateway {

    private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    override suspend fun getAccountByPuuid(puuid: String): RiotAccountDto {
        logger.debug("Fetching account for '$puuid'...")
        val response: HttpResponse = client.get("$baseUrl/riot/account/v1/accounts/by-puuid/$puuid") {
            headers {
                append("X-Riot-Token", apiKey)
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                logger.debug("Successfully fetched account.")
                response.body<RiotAccountDto>()
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