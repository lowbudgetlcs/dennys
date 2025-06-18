package com.lowbudgetlcs.http

import com.lowbudgetlcs.RiotConfig
import com.lowbudgetlcs.configBinder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RiotApiClient(
    private val client: HttpClient = defaultClient(),
    private val apiKey: String = configBinder.bindOrThrow<RiotConfig>("riot").key
) {
    companion object {
        fun defaultClient(): HttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    suspend fun get(url: String): HttpResponse {
        return client.get(url) {
            headers {
                append("X-Riot-Token", apiKey)
            }
        }
    }
}