package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.routes.dto.riot.account.RiotAccountDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class RiotAccountGateway(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://americas.api.riotgames.com"
) : IRiotAccountGateway {

    override suspend fun getAccountByPuuid(puuid: String): RiotAccountDto {
        val response: HttpResponse = client.get("$baseUrl/riot/account/v1/accounts/by-puuid/$puuid") {
            headers {
                append("X-Riot-Token", apiKey)
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.BadRequest -> throw IllegalArgumentException("Invalid Riot PUUID")
            HttpStatusCode.NotFound -> throw NoSuchElementException("Riot account not found for PUUID")
            else -> throw RiotApiException("Unexpected Riot API error: ${response.status}")
        }
    }
}