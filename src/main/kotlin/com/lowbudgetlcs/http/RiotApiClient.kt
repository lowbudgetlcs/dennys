package com.lowbudgetlcs.http

import com.lowbudgetlcs.config.RiotConfigLoader
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RiotApiClient(
    private val client: HttpClient = defaultClient(),
    private val rateLimiter: RateLimiter = RateLimiter(),
    private val apiKey: String = RiotConfigLoader.config.apiKey
) {
    companion object {
        fun defaultClient(): HttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    /*
    The RateLimiting functionality is baked into the HttpClient, so
    callers don't need to worry about it.
     */
    suspend fun get(url: String): HttpResponse {
        rateLimiter.acquire(url)
        val response = client.get(url) {
            headers {
                append("X-Riot-Token", apiKey)
            }
        }
        rateLimiter.updateLimits(response, url)
        return response
    }
}