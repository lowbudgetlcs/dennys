package routes

import com.lowbudgetlcs.api.routes.v1.stats.statsRoutesV1
import com.lowbudgetlcs.api.setupStatusPages
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.stats.StatsService
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.repositories.stats.IStatsRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk

private fun getStats(
    repo: IStatsRepository,
    path: String,
    check: suspend (HttpResponse) -> Unit,
) = testApplication {
    install(Resources)
    install(ContentNegotiation) { json() }
    application {
        setupStatusPages()
        routing { statsRoutesV1(StatsService(mockk(), repo)) }
    }
    check(client.get(path))
}

class StatsRouteTest :
    StringSpec({
        "every player-games filter and the page reach the repository" {
            val repo = mockk<IStatsRepository>()
            val query =
                StatsQuery(EventGroupId(16), EventId(2), EventStage.PLAYOFFS, TeamId(7), PlayerId(31), championId = 103)
            every { repo.getPlayerGames(query, 25, 50) } returns emptyList()

            getStats(
                repo,
                "/stats/player-games?eventGroupId=16&eventId=2&stage=PLAYOFFS&teamId=7&playerId=31&championId=103&limit=25&offset=50",
            ) {
                it.status shouldBe HttpStatusCode.OK
                it.bodyAsText() shouldBe "[]"
            }
        }

        "team-games without filters pages 100 from the start" {
            val repo = mockk<IStatsRepository>()
            every { repo.getTeamGames(StatsQuery(), 100, 0) } returns emptyList()

            getStats(repo, "/stats/team-games") { it.status shouldBe HttpStatusCode.OK }
        }

        "team-games passes playerId through" {
            val repo = mockk<IStatsRepository>()
            every { repo.getTeamGames(StatsQuery(playerId = PlayerId(31)), 100, 0) } returns emptyList()

            getStats(repo, "/stats/team-games?playerId=31") { it.status shouldBe HttpStatusCode.OK }
        }

        "an unknown stage is a 400 and an out-of-range limit is a 422" {
            val repo = mockk<IStatsRepository>()

            getStats(repo, "/stats/player-games?stage=FINALS") { it.status shouldBe HttpStatusCode.BadRequest }
            getStats(repo, "/stats/team-games?limit=0") { it.status shouldBe HttpStatusCode.UnprocessableEntity }
            getStats(repo, "/stats/player-games?limit=1001") { it.status shouldBe HttpStatusCode.UnprocessableEntity }
        }
    })
