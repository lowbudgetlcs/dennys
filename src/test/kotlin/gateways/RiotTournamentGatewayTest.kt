package gateways

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.tournament.RiotRegion
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGateway
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val WINNER_PUUID_1 = "w1-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
private const val WINNER_PUUID_2 = "w2-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

private val GAMES_BY_CODE_PAYLOAD =
    """
    [
      {
        "startTime": 1752537124000,
        "winningTeam": [ { "puuid": "$WINNER_PUUID_1" }, { "puuid": "$WINNER_PUUID_2" } ],
        "losingTeam": [ { "puuid": "$WINNER_PUUID_1" } ],
        "shortCode": "NA04eff-c5b0b774-c049-47cf-88f4-eee05c8d83ae",
        "metaData": "{\"seriesId\":42}",
        "gameId": 5102531894,
        "gameName": "test-game",
        "gameType": "TOURNAMENT",
        "gameMap": 11,
        "gameMode": "CLASSIC",
        "region": "NA"
      }
    ]
    """.trimIndent()

private fun gatewayOf(engine: MockEngine) =
    RiotTournamentGateway(
        client =
            HttpClient(engine) {
                install(HttpRequestRetry) {
                    maxRetries = 3
                    retryIf { _, response -> response.status == HttpStatusCode.TooManyRequests }
                    delayMillis { 0 }
                }
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            },
        apiKey = "test-key",
        useStubs = false,
        providerId = 1,
    )

class RiotTournamentGatewayTest :
    StringSpec({
        val shortcode = Shortcode("NA04eff-c5b0b774-c049-47cf-88f4-eee05c8d83ae")

        "getGames parses a realistic games-by-code payload" {
            val gateway =
                gatewayOf(
                    MockEngine {
                        respond(
                            GAMES_BY_CODE_PAYLOAD,
                            HttpStatusCode.OK,
                            headersOf("Content-Type", ContentType.Application.Json.toString()),
                        )
                    },
                )

            val games = gateway.getGames(shortcode)

            games.size shouldBe 1
            games.first().gameId shouldBe 5102531894L
            games.first().region shouldBe "NA"
            games.first().winningTeam.map { it.puuid } shouldBe listOf(WINNER_PUUID_1, WINNER_PUUID_2)
        }

        "getGames treats 404 as Riot answering with no game" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.NotFound) })

            gateway.getGames(shortcode).shouldBeEmpty()
        }

        "getGames throws rather than reporting empty when Riot is unreachable" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) })

            shouldThrow<RiotApiException> { gateway.getGames(shortcode) }
        }

        "a 429 is retried rather than failing outright" {
            var calls = 0
            val gateway =
                gatewayOf(
                    MockEngine {
                        calls++
                        if (calls < 3) {
                            respondError(HttpStatusCode.TooManyRequests)
                        } else {
                            respond(
                                GAMES_BY_CODE_PAYLOAD,
                                HttpStatusCode.OK,
                                headersOf("Content-Type", ContentType.Application.Json.toString()),
                            )
                        }
                    },
                )

            gateway.getGames(shortcode).size shouldBe 1
            calls shouldBe 3
        }

        "the tournament region enum maps to a platformId" {
            RiotRegion.platformIdOf("NA") shouldBe "NA1"
            RiotRegion.platformIdOf("EUW") shouldBe "EUW1"
            RiotRegion.platformIdOf("KR") shouldBe "KR"
            RiotRegion.platformIdOf("NOT_A_REGION") shouldBe null
        }
    })
