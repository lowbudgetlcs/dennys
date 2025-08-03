package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.routes.dto.riot.account.AccountDto
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

    override suspend fun getAccountByPuuid(puuid: String): AccountDto? {
        val response: HttpResponse = client.get("$baseUrl/riot/account/v1/accounts/by-puuid/$puuid") {
            headers {
                append("X-Riot-Token", apiKey)
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.BadRequest -> null
            HttpStatusCode.NotFound -> null
            else -> throw RuntimeException("Riot API error: ${response.status}")
        }
    }
}