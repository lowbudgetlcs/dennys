import com.lowbudgetlcs.RateLimiter
import com.lowbudgetlcs.RiotApiClient
import com.lowbudgetlcs.repositories.riot.RiotMatchRepository
import com.lowbudgetlcs.routes.dto.riot.match.MatchDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

class RiotMatchRepositoryTest : FunSpec({

    xtest("getMatch should return MatchDto when API call succeeds") {
        // Mock dependencies
        val apiClient = mockk<RiotApiClient>()
        val rateLimiter = mockk<RateLimiter>(relaxed = true)
        val response = mockk<HttpResponse>()
        val expectedMatch = mockk<MatchDto>()

        // Mock API response
        every { response.status } returns HttpStatusCode.Companion.OK
        coEvery { response.body<MatchDto>() } returns expectedMatch
        coEvery { apiClient.get(any()) } returns response

        // Create test instance
        val repository = RiotMatchRepository(apiClient, rateLimiter)

        // Run test
        val result = repository.getMatch(5240057151)

        // Assertions
        result shouldBe expectedMatch
        coVerify { apiClient.get("https://americas.api.riotgames.com/lol/match/v5/matches/NA1_5240057151") }
    }

    xtest("getMatch should return null when API responds with an error") {
        val apiClient = mockk<RiotApiClient>()
        val rateLimiter = mockk<RateLimiter>(relaxed = true)
        val response = mockk<HttpResponse>()

        // Mock error response
        every { response.status } returns HttpStatusCode.Companion.BadRequest
        coEvery { apiClient.get(any()) } returns response

        val repository = RiotMatchRepository(apiClient, rateLimiter)

        // Run test
        val result = repository.getMatch(5240057151)

        // Assertions
        result shouldBe null
    }

    xtest("getMatch should return null when an exception is thrown") {
        val apiClient = mockk<RiotApiClient>()
        val rateLimiter = mockk<RateLimiter>(relaxed = true)

        // Simulate an API failure
        coEvery { apiClient.get(any()) } throws RuntimeException("API error")

        val repository = RiotMatchRepository(apiClient, rateLimiter)

        // Run test
        val result = repository.getMatch(5240057151)

        // Assertions
        result shouldBe null
    }
})