package routes

import com.lowbudgetlcs.api.routes.riotCallbackRoutes
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.models.RefreshOutcome
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot

private const val ROUTE_SHORTCODE = "NA04eff-c5b0b774-c049-47cf-88f4-eee05c8d83ae"

/** The body relay forwards byte-for-byte from Riot, per lowbudgetlcs/relay main.go RiotPayload. */
private fun relayPayload(extra: String = "") =
    """
    {
      "startTime": 1752534724000,
      "shortCode": "$ROUTE_SHORTCODE",
      "metaData": "{\"tag\":\"LBLCS\",\"seriesId\":90}",
      "gameId": 5102531894,
      "gameName": "cb0b0b1e-0000-0000-0000-000000000000",
      "gameType": "Practice",
      "gameMap": 11,
      "gameMode": "CLASSIC",
      "region": "NA1"$extra
    }
    """.trimIndent()

class RiotCallbackRouteTest :
    StringSpec({
        "a relay-forwarded callback reaches the service with the shortcode Riot named" {
            val service = mockk<ISeriesService>(relaxed = false)
            val shortcode = slot<Shortcode>()
            coEvery { service.refreshFromShortcode(capture(shortcode)) } returns RefreshOutcome.ATTRIBUTED

            testApplication {
                application { routing { riotCallbackRoutes(service) } }

                val response: HttpResponse =
                    client.post("/riot-callback") {
                        contentType(ContentType.Application.Json)
                        setBody(relayPayload())
                    }

                response.status shouldBe HttpStatusCode.OK
            }

            shortcode.captured shouldBe Shortcode(ROUTE_SHORTCODE)
        }

        "a field Riot adds later does not reject the callback" {
            val service = mockk<ISeriesService>(relaxed = false)
            coEvery { service.refreshFromShortcode(any()) } returns RefreshOutcome.ATTRIBUTED

            testApplication {
                application { routing { riotCallbackRoutes(service) } }

                val response: HttpResponse =
                    client.post("/riot-callback") {
                        contentType(ContentType.Application.Json)
                        setBody(relayPayload(extra = ""","somethingRiotAddedLater": "value""""))
                    }

                response.status shouldBe HttpStatusCode.OK
            }

            coVerify(exactly = 1) { service.refreshFromShortcode(Shortcode(ROUTE_SHORTCODE)) }
        }

        "an unknown shortcode is still answered with 200 so Riot does not retry" {
            val service = mockk<ISeriesService>(relaxed = false)
            coEvery { service.refreshFromShortcode(any()) } returns null

            testApplication {
                application { routing { riotCallbackRoutes(service) } }

                val response: HttpResponse =
                    client.post("/riot-callback") {
                        contentType(ContentType.Application.Json)
                        setBody(relayPayload())
                    }

                response.status shouldBe HttpStatusCode.OK
            }
        }
    })
