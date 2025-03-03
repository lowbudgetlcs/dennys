package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.models.match.LeagueOfLegendsMatch
import com.lowbudgetlcs.util.RateLimiter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

class RiotMatchRepositoryTest : StringSpec({

    "getMatch should return LeagueOfLegendsMatch when API call succeeds" {
        // Mock dependencies
        val apiClient = mockk<RiotApiClient>()
        val rateLimiter = mockk<RateLimiter>(relaxed = true)
        val response = mockk<HttpResponse>()
        val expectedMatch = mockk<LeagueOfLegendsMatch>()

        // Mock API response
        every { response.status } returns HttpStatusCode.OK
        coEvery { response.body<LeagueOfLegendsMatch>() } returns expectedMatch
        coEvery { apiClient.get(any()) } returns response

        // Create test instance
        val repository = RiotMatchRepositoryImpl(apiClient, rateLimiter)

        // Run test
        val result = repository.getMatch(5240057151)

        // Assertions
        result shouldBe expectedMatch
        coVerify { apiClient.get("https://americas.api.riotgames.com/lol/match/v5/matches/NA1_5240057151") }
    }

    "getMatch should return null when API responds with an error" {
        val apiClient = mockk<RiotApiClient>()
        val rateLimiter = mockk<RateLimiter>(relaxed = true)
        val response = mockk<HttpResponse>()

        // Mock error response
        every { response.status } returns HttpStatusCode.BadRequest
        coEvery { apiClient.get(any()) } returns response

        val repository = RiotMatchRepositoryImpl(apiClient, rateLimiter)

        // Run test
        val result = repository.getMatch(5240057151)

        // Assertions
        result shouldBe null
    }

    "getMatch should return null when an exception is thrown" {
        val apiClient = mockk<RiotApiClient>()
        val rateLimiter = mockk<RateLimiter>(relaxed = true)

        // Simulate an API failure
        coEvery { apiClient.get(any()) } throws RuntimeException("API error")

        val repository = RiotMatchRepositoryImpl(apiClient, rateLimiter)

        // Run test
        val result = repository.getMatch(5240057151)

        // Assertions
        result shouldBe null
    }
})