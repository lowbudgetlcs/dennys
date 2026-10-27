package services

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.ITournamentCodeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant

private val HOME = TeamId(1)
private val AWAY = TeamId(2)
private val OUTSIDER = TeamId(3)
private val REPORT_SERIES_ID = SeriesId(50)
private val REPORT_CREATED_AT = Instant.parse("2026-07-14T23:12:04Z")

private class ReportFixture {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway)

    val games = mutableListOf<Game>()
    private var nextId = 100

    val series =
        Series(
            id = REPORT_SERIES_ID,
            eventId = EventId(1),
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(HOME, AWAY),
            result = null,
            status = SeriesStatus.NOT_STARTED,
        )

    fun code(
        id: Int,
        shortcode: String,
    ) = TournamentCode(
        id = TournamentCodeId(id),
        shortcode = Shortcode(shortcode),
        blueTeamId = HOME,
        redTeamId = AWAY,
        seriesId = REPORT_SERIES_ID,
        createdAt = Instant.parse("2026-07-14T22:58:31Z"),
    )

    fun recordedGame(
        codeId: Int?,
        result: GameResult? = GameResult(HOME, AWAY),
    ) = Game(
        id = GameId(nextId++),
        seriesId = REPORT_SERIES_ID,
        tournamentCodeId = codeId?.let { TournamentCodeId(it) },
        riotMatchId = null,
        number = games.size + 1,
        createdAt = REPORT_CREATED_AT,
        result = result,
    )

    fun withSeries(
        codes: List<TournamentCode>,
        recorded: List<Game> = emptyList(),
    ) {
        games.clear()
        games += recorded
        every { seriesRepo.getById(REPORT_SERIES_ID) } returns series
        every { seriesRepo.markInProgress(any()) } returns Unit
        every { gameRepo.getBySeriesId(REPORT_SERIES_ID) } answers { games.toList() }
        every { codeRepo.getBySeriesId(REPORT_SERIES_ID) } returns codes
        codes.forEach {
            every { codeRepo.getById(it.id) } returns it
            every { codeRepo.getByShortcode(it.shortcode) } returns it
        }
        every { gameRepo.insert(any()) } answers {
            val new = firstArg<NewGame>()
            val game =
                Game(
                    id = GameId(nextId++),
                    seriesId = new.seriesId,
                    tournamentCodeId = new.tournamentCodeId,
                    riotMatchId = new.riotMatchId,
                    number = games.size + 1,
                    createdAt = REPORT_CREATED_AT,
                    result = new.result,
                )
            games += game
            game
        }
        every { seriesRepo.setStatus(any(), any(), any(), any()) } returns series.copy(status = SeriesStatus.COMPLETE)
    }
}

private fun reported(
    winner: TeamId? = null,
    loser: TeamId? = null,
    codeId: Int? = null,
    shortcode: String? = null,
) = ReportedResult(
    winningTeamId = winner,
    losingTeamId = loser,
    tournamentCodeId = codeId?.let { TournamentCodeId(it) },
    shortcode = shortcode?.let { Shortcode(it) },
)

class SeriesReportResultTest :
    StringSpec({
        "targeting by tournamentCodeId attaches to that exact code" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A"), f.code(2, "SHORT-B")))

            val outcome = f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 2))

            outcome.recorded shouldBe true
            outcome.game.tournamentCodeId shouldBe TournamentCodeId(2)
            outcome.game.result shouldBe GameResult(HOME, AWAY)
        }

        "targeting by shortcode attaches to that exact code" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A"), f.code(2, "SHORT-B")))

            val outcome = f.service.reportResult(REPORT_SERIES_ID, reported(AWAY, shortcode = "SHORT-A"))

            outcome.recorded shouldBe true
            outcome.game.tournamentCodeId shouldBe TournamentCodeId(1)
            outcome.game.result shouldBe GameResult(AWAY, HOME)
        }

        "giving both tournamentCodeId and shortcode is rejected" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            shouldThrow<IllegalArgumentException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 1, shortcode = "SHORT-A"))
            }

            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "a report with no code is recorded codeless without asking Riot" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A"), f.code(2, "SHORT-B")))

            val outcome = f.service.reportResult(REPORT_SERIES_ID, reported(HOME))

            outcome.recorded shouldBe true
            outcome.game.tournamentCodeId shouldBe null
            outcome.game.riotMatchId shouldBe null
            f.games.count { it.tournamentCodeId != null } shouldBe 0
            coVerify(exactly = 0) { f.gateway.getGames(any()) }
        }

        "reporting a code that already has a result returns the existing game" {
            val f = ReportFixture()
            val existing = f.recordedGame(codeId = 1)
            f.withSeries(listOf(f.code(1, "SHORT-A")), recorded = listOf(existing))

            val outcome = f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 1))

            outcome.recorded shouldBe false
            outcome.game shouldBe existing
            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "a report with no winner is rejected" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            shouldThrow<IllegalArgumentException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(codeId = 1))
            }

            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "a code belonging to another series is not found" {
            val f = ReportFixture()
            val foreign = f.code(9, "SHORT-FOREIGN").copy(seriesId = SeriesId(51))
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.codeRepo.getById(TournamentCodeId(9)) } returns foreign

            shouldThrow<NoSuchElementException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 9))
            }
        }

        "an unknown shortcode is not found" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.codeRepo.getByShortcode(Shortcode("NOPE")) } returns null

            shouldThrow<NoSuchElementException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(HOME, shortcode = "NOPE"))
            }
        }

        "a winner outside the series is rejected" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            shouldThrow<IllegalArgumentException> { f.service.reportResult(REPORT_SERIES_ID, reported(OUTSIDER)) }
        }

        "a loser that contradicts the winner is rejected" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            shouldThrow<IllegalArgumentException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(winner = HOME, loser = HOME))
            }
        }

        "a loser without a winner is rejected" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            shouldThrow<IllegalArgumentException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(loser = AWAY))
            }
        }

        "a report that clinches the series closes it" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")), recorded = listOf(f.recordedGame(codeId = 1)))

            f.service.reportResult(REPORT_SERIES_ID, reported(HOME))

            verify(exactly = 1) {
                f.seriesRepo.setStatus(REPORT_SERIES_ID, SeriesStatus.COMPLETE, SeriesResult(HOME, AWAY), false)
            }
        }

        "a mixed series counts coded and codeless games alike" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")), recorded = listOf(f.recordedGame(codeId = 1)))

            f.service.reportResult(REPORT_SERIES_ID, reported(HOME))

            f.games.map { it.tournamentCodeId } shouldBe listOf(TournamentCodeId(1), null)
            f.games.mapNotNull { it.result?.winningTeamId } shouldBe listOf(HOME, HOME)
        }

        "a report on a double-loss series records the game and keeps the double loss" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.seriesRepo.getById(REPORT_SERIES_ID) } returns
                f.series.copy(status = SeriesStatus.COMPLETE, doubleLoss = true)

            val outcome = f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 1))

            outcome.recorded shouldBe true
            verify(exactly = 1) { f.gameRepo.insert(any()) }
            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
        }

        "a report that loses the insert race returns the game that won it" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            val winner = f.recordedGame(codeId = 1)
            every { f.gameRepo.insert(any()) } answers {
                f.games += winner
                throw IllegalStateException("Tournament code '1' already has a game recorded.")
            }

            val outcome = f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 1))

            outcome.recorded shouldBe false
            outcome.game shouldBe winner
        }

        "a failure closing the series after the insert is not reported as a lost race" {
            val f = ReportFixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")), recorded = listOf(f.recordedGame(codeId = null)))
            every { f.seriesRepo.setStatus(any(), any(), any(), any()) } throws IllegalStateException("boom")

            shouldThrow<IllegalStateException> {
                f.service.reportResult(REPORT_SERIES_ID, reported(HOME, codeId = 1))
            }.message shouldBe "boom"
            f.games.size shouldBe 2
        }
    })
