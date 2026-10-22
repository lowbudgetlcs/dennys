package routes

import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.types.TeamId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk

class SeriesPatchRouteTest :
    StringSpec({
        "PATCH /series/{id} passes the body to patchSeries and returns the double loss" {
            val service = mockk<ISeriesService>()
            every { service.patchSeries(SeriesId(3), SeriesStatus.COMPLETE, null, true) } returns
                Series(
                    id = SeriesId(3),
                    eventId = EventId(1),
                    eventStage = EventStage.REGULAR_SEASON,
                    totalGames = 1,
                    participants = TeamId(1) to TeamId(2),
                    result = null,
                    status = SeriesStatus.COMPLETE,
                    doubleLoss = true,
                )

            testApplication {
                install(Resources)
                install(ContentNegotiation) { json() }
                application { routing { seriesRoutesV1(service) } }

                val patched =
                    client.patch("/series/3") {
                        contentType(ContentType.Application.Json)
                        setBody("""{"status":"COMPLETE","doubleLoss":true}""")
                    }
                patched.status shouldBe HttpStatusCode.OK
                patched.bodyAsText() shouldContain "\"doubleLoss\":true"

                client.post("/series/3/complete").status shouldBe HttpStatusCode.NotFound
            }
        }
    })
