package services

import com.lowbudgetlcs.domain.series.game.models.RiotMatchData
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.stats.StatsService
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.TeamGameStats
import com.lowbudgetlcs.repositories.riotmatchdata.IRiotMatchDataRepository
import com.lowbudgetlcs.repositories.stats.IStatsRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

private val GAME = 1.toGameId()
private val MATCH = buildJsonObject { put("matchId", JsonPrimitive("NA1_1")) }
private val TIMELINE = buildJsonObject { put("frameInterval", JsonPrimitive(60_000)) }

private class StatsFixture {
    val dataRepo = mockk<IRiotMatchDataRepository>()
    val statsRepo = mockk<IStatsRepository>()
    val service = StatsService(dataRepo, statsRepo)
}

class StatsServiceTest :
    StringSpec({
        "returns the stored match and timeline" {
            val f = StatsFixture()
            every { f.dataRepo.getByGameId(GAME) } returns RiotMatchData(GAME, MATCH, TIMELINE, Instant.EPOCH)

            f.service.getMatch(GAME) shouldBe MATCH
            f.service.getTimeline(GAME) shouldBe TIMELINE
        }

        "a game without match data is not found on every endpoint" {
            val f = StatsFixture()
            every { f.dataRepo.getByGameId(GAME) } returns null
            every { f.statsRepo.getGameStats(GAME) } returns GameStats(emptyList(), emptyList())

            shouldThrow<NoSuchElementException> { f.service.getMatch(GAME) }
            shouldThrow<NoSuchElementException> { f.service.getTimeline(GAME) }
            shouldThrow<NoSuchElementException> { f.service.getGameStats(GAME) }
        }

        "a match stored without a timeline serves the match but not the timeline" {
            val f = StatsFixture()
            every { f.dataRepo.getByGameId(GAME) } returns RiotMatchData(GAME, MATCH, null, Instant.EPOCH)

            f.service.getMatch(GAME) shouldBe MATCH
            shouldThrow<NoSuchElementException> { f.service.getTimeline(GAME) }
        }

        "returns the stats rows for an ingested game" {
            val f = StatsFixture()
            val stats = GameStats(emptyList(), listOf(mockkClass(TeamGameStats::class)))
            every { f.statsRepo.getGameStats(GAME) } returns stats

            f.service.getGameStats(GAME) shouldBe stats
        }

        "row lookups reject a limit outside 1..1000 or a negative offset" {
            val f = StatsFixture()
            listOf(0 to 0, 1001 to 0, 100 to -1).forEach { (limit, offset) ->
                shouldThrow<IllegalArgumentException> { f.service.getPlayerGames(StatsQuery(), limit, offset) }
                shouldThrow<IllegalArgumentException> { f.service.getTeamGames(StatsQuery(), limit, offset) }
            }
            verify(exactly = 0) { f.statsRepo.getPlayerGames(any(), any(), any()) }
            verify(exactly = 0) { f.statsRepo.getTeamGames(any(), any(), any()) }
        }

        "row lookups pass the query and page through" {
            val f = StatsFixture()
            val query = StatsQuery(championId = 103)
            every { f.statsRepo.getPlayerGames(query, 1000, 50) } returns emptyList()
            every { f.statsRepo.getTeamGames(query, 1, 0) } returns emptyList()

            f.service.getPlayerGames(query, 1000, 50) shouldBe emptyList()
            f.service.getTeamGames(query, 1, 0) shouldBe emptyList()
        }
    })
