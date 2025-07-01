package com.lowbudgetlcs.http

import com.lowbudgetlcs.config.RiotConfigLoader
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object RiotApiClient {

    val riotHttpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    val rateLimiter: RateLimiter = RateLimiter()
    val apiKey: String = RiotConfigLoader.config.apiKey

    /*
    The RateLimiting functionality is baked into the HttpClient, so
    callers don't need to worry about it.
     */
    suspend fun get(url: String): HttpResponse {
        rateLimiter.acquire(url)
        val response = riotHttpClient.get(url) {
            headers {
                append("X-Riot-Token", apiKey)
            }
        }
        rateLimiter.updateLimits(response, url)
        return response
    }
}