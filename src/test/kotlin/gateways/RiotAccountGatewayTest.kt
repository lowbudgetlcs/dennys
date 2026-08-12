package gateways

import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.account.RiotAccountGateway
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** The account from the production report that returned 422 for a PUUID Riot itself accepts. */
private const val FULL_METAL_PUUID = "wtATP4ScufpCsr8o7FaDd2wQu5u3dZjPWLiU-W0dJ41EVO_JuSrDDBV680ygdBARc7H7GARUTtJjeA"

private val ACCOUNT_PAYLOAD =
    """
    {
      "puuid": "$FULL_METAL_PUUID",
      "gameName": "Full Metal",
      "tagLine": "FLAME"
    }
    """.trimIndent()

private fun gatewayOf(engine: MockEngine) =
    RiotAccountGateway(
        client =
            HttpClient(engine) {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            },
        apiKey = "test-key",
    )

/** Records the single request the gateway makes so its URL can be asserted on. */
private class Recorder {
    var request: HttpRequestData? = null

    fun ok() =
        MockEngine { req ->
            request = req
            respond(
                ACCOUNT_PAYLOAD,
                HttpStatusCode.OK,
                headersOf("Content-Type", ContentType.Application.Json.toString()),
            )
        }

    val url: String get() = request?.url?.toString() ?: error("no request was made")
}

class RiotAccountGatewayTest :
    StringSpec({
        val puuid = Puuid(FULL_METAL_PUUID)

        "the request path carries the bare PUUID, not the wrapper type's toString" {
            val recorder = Recorder()

            gatewayOf(recorder.ok()).getAccountByPuuid(puuid)

            recorder.url shouldContain "/riot/account/v1/accounts/by-puuid/$FULL_METAL_PUUID"
            recorder.url shouldNotContain "Puuid("
            recorder.url shouldNotContain "value="
        }

        "the PUUID is the final path segment, with nothing appended" {
            val recorder = Recorder()

            gatewayOf(recorder.ok()).getAccountByPuuid(puuid)

            recorder.url.substringAfterLast('/') shouldBe FULL_METAL_PUUID
        }

        "the API key is sent as X-Riot-Token" {
            val recorder = Recorder()

            gatewayOf(recorder.ok()).getAccountByPuuid(puuid)

            recorder.request?.headers?.get("X-Riot-Token") shouldBe "test-key"
        }

        "a 200 maps the payload onto the domain account" {
            val gateway = gatewayOf(Recorder().ok())

            gateway.getAccountByPuuid(puuid).riotPuuid shouldBe puuid
        }

        "a 400 is an invalid-PUUID argument failure" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.BadRequest) })

            val ex = shouldThrow<IllegalArgumentException> { gateway.getAccountByPuuid(puuid) }

            ex.message.toString() shouldContain "Invalid Riot PUUID"
        }

        "a 404 is a missing account, distinct from a malformed one" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.NotFound) })

            shouldThrow<NoSuchElementException> { gateway.getAccountByPuuid(puuid) }
        }

        "a 403 is a Riot API failure, not the caller's fault" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.Forbidden) })

            val ex = shouldThrow<RiotApiException> { gateway.getAccountByPuuid(puuid) }

            ex.status shouldBe 403
            ex.retryable shouldBe false
        }

        "a 429 is reported as retryable" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.TooManyRequests) })

            shouldThrow<RiotApiException> { gateway.getAccountByPuuid(puuid) }.retryable shouldBe true
        }

        "a 500 is reported as retryable" {
            val gateway = gatewayOf(MockEngine { respondError(HttpStatusCode.InternalServerError) })

            shouldThrow<RiotApiException> { gateway.getAccountByPuuid(puuid) }.retryable shouldBe true
        }
    })
