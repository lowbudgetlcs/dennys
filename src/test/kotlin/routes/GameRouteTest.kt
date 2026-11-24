package routes

import com.lowbudgetlcs.api.routes.v1.game.gameRoutesV1
import com.lowbudgetlcs.api.setupStatusPages
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.stats.IStatsService
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.TeamGameStats
import com.lowbudgetlcs.domain.team.models.types.TeamId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

private val GAME = 7.toGameId()
private val TEAM =
    TeamGameStats(
        gameId = GAME,
        seriesId = SeriesId(3),
        eventId = EventId(2),
        eventGroupId = null,
        stage = EventStage.PLAYOFFS,
        teamId = TeamId(1),
        side = "BLUE",
        gameDurationSeconds = 1800,
        win = true,
        firstBlood = true,
        firstTower = false,
        firstDragon = false,
        firstBaron = false,
        kills = 12,
        towerKills = 9,
        inhibitorKills = 1,
        dragonKills = 3,
        baronKills = 1,
        riftHeraldKills = 0,
        hordeKills = 4,
        bans = listOf(55, 84),
    )

private fun getGame(
    service: IStatsService,
    path: String,
    check: suspend (HttpResponse) -> Unit,
) = testApplication {
    install(Resources)
    install(ContentNegotiation) { json() }
    application {
        setupStatusPages()
        routing { gameRoutesV1(service) }
    }
    check(client.get(path))
}

class GameRouteTest :
    StringSpec({
        "match and timeline are served as stored and cached as immutable" {
            val service = mockk<IStatsService>()
            every { service.getMatch(GAME) } returns buildJsonObject { put("matchId", JsonPrimitive("NA1_1")) }
            every { service.getTimeline(GAME) } returns buildJsonObject { put("frameInterval", JsonPrimitive(60_000)) }

            getGame(service, "/games/7/match") {
                it.status shouldBe HttpStatusCode.OK
                it.headers[HttpHeaders.CacheControl] shouldBe "private, max-age=31536000, immutable"
                it.bodyAsText() shouldBe """{"matchId":"NA1_1"}"""
            }
            getGame(service, "/games/7/timeline") {
                it.status shouldBe HttpStatusCode.OK
                it.headers[HttpHeaders.CacheControl] shouldBe "private, max-age=31536000, immutable"
                it.bodyAsText() shouldBe """{"frameInterval":60000}"""
            }
        }

        "stats are players and teams, cached briefly" {
            val service = mockk<IStatsService>()
            every { service.getGameStats(GAME) } returns GameStats(emptyList(), listOf(TEAM))

            getGame(service, "/games/7/stats") {
                it.status shouldBe HttpStatusCode.OK
                it.headers[HttpHeaders.CacheControl] shouldBe "private, max-age=300"
                val body = it.bodyAsText()
                body shouldContain "\"players\":[]"
                body shouldContain "\"teams\":[{\"gameId\":7"
                body shouldContain "\"stage\":\"PLAYOFFS\""
                body shouldContain "\"bans\":[55,84]"
            }
        }

        "a game without match data is a 404 with no cache header" {
            val service = mockk<IStatsService>()
            every { service.getMatch(any()) } throws NoSuchElementException("none")
            every { service.getTimeline(any()) } throws NoSuchElementException("none")
            every { service.getGameStats(any()) } throws NoSuchElementException("none")

            listOf("match", "timeline", "stats").forEach { endpoint ->
                getGame(service, "/games/7/$endpoint") {
                    it.status shouldBe HttpStatusCode.NotFound
                    it.headers[HttpHeaders.CacheControl].shouldBeNull()
                }
            }
        }
    })
