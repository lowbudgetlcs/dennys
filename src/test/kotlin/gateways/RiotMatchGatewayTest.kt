package gateways

import com.lowbudgetlcs.config.modules.riotJson
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.match.RiotMatchGateway
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val MATCH_PAYLOAD =
    """{"metadata":{"matchId":"NA1_5102531894"},"info":{"atakhan":{"kills":1},"newRiotThing":[1,null,"x"]}}"""

private fun gatewayOf(engine: MockEngine) =
    RiotMatchGateway(
        client = HttpClient(engine) { install(ContentNegotiation) { json(riotJson) } },
        apiKey = "test-key",
    )

private fun okWith(
    body: String,
    seenUrls: MutableList<String>,
) = MockEngine { request ->
    seenUrls += request.url.toString()
    respond(body, HttpStatusCode.OK, headersOf("Content-Type", ContentType.Application.Json.toString()))
}

class RiotMatchGatewayTest :
    StringSpec({
        val matchId = "NA1_5102531894".toRiotMatchId()

        "getMatch returns Riot's body unchanged from the americas match-v5 route" {
            val urls = mutableListOf<String>()

            val match = gatewayOf(okWith(MATCH_PAYLOAD, urls)).getMatch(matchId)

            match shouldBe Json.parseToJsonElement(MATCH_PAYLOAD)
            urls shouldBe listOf("https://americas.api.riotgames.com/lol/match/v5/matches/NA1_5102531894")
        }

        "getTimeline calls the timeline route" {
            val urls = mutableListOf<String>()

            gatewayOf(okWith("{}", urls)).getTimeline(matchId)

            urls shouldBe listOf("https://americas.api.riotgames.com/lol/match/v5/matches/NA1_5102531894/timeline")
        }

        "a 404 is NoSuchElementException" {
            shouldThrow<NoSuchElementException> {
                gatewayOf(MockEngine { respondError(HttpStatusCode.NotFound) }).getMatch(matchId)
            }
        }

        "any other failure is RiotApiException with the status" {
            val e =
                shouldThrow<RiotApiException> {
                    gatewayOf(MockEngine { respondError(HttpStatusCode.InternalServerError) }).getMatch(matchId)
                }
            e.status shouldBe 500
        }
    })
