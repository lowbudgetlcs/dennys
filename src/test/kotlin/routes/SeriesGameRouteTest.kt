package routes

import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.api.setupStatusPages
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.IssueOutcome
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.repositories.ConflictException
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import java.time.Instant

private val NEW = NewTournamentCode(SeriesId(3), TeamId(1), TeamId(2))
private val CODE =
    TournamentCode(
        id = TournamentCodeId(9),
        shortcode = Shortcode("NA04a-1f3c"),
        blueTeamId = TeamId(1),
        redTeamId = TeamId(2),
        seriesId = SeriesId(3),
        createdAt = Instant.parse("2026-10-22T20:10:00Z"),
    )

private fun postGame(
    service: ISeriesService,
    body: String,
    check: suspend (HttpResponse) -> Unit,
) = testApplication {
    install(Resources)
    install(ContentNegotiation) { json() }
    application {
        setupStatusPages()
        routing { seriesRoutesV1(service) }
    }
    check(
        client.post("/series/3/game") {
            contentType(ContentType.Application.Json)
            setBody(body)
        },
    )
}

class SeriesGameRouteTest :
    StringSpec({
        "a newly issued code is 201 and a reused one is 200" {
            val service = mockk<ISeriesService>()
            coEvery { service.createGame(NEW, false) } returns IssueOutcome(CODE, issued = true)
            postGame(service, """{"blueTeamId":1,"redTeamId":2}""") { it.status shouldBe HttpStatusCode.Created }

            coEvery { service.createGame(NEW, false) } returns IssueOutcome(CODE, issued = false)
            postGame(service, """{"blueTeamId":1,"redTeamId":2}""") { it.status shouldBe HttpStatusCode.OK }
        }

        "newCode in the body reaches the service as forceNew" {
            val service = mockk<ISeriesService>()
            coEvery { service.createGame(NEW, true) } returns IssueOutcome(CODE, issued = true)

            postGame(service, """{"blueTeamId":1,"redTeamId":2,"newCode":true}""") {
                it.status shouldBe HttpStatusCode.Created
            }
        }

        "the code cap is a 409 that says CODE_LIMIT" {
            val service = mockk<ISeriesService>()
            coEvery { service.createGame(NEW, true) } throws ConflictException("Cap reached.", reason = "CODE_LIMIT")

            postGame(service, """{"blueTeamId":1,"redTeamId":2,"newCode":true}""") {
                it.status shouldBe HttpStatusCode.Conflict
                it.bodyAsText() shouldContain "\"reason\":\"CODE_LIMIT\""
            }
        }
    })
