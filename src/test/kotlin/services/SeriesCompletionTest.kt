package services

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.types.SeriesId
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Instant

private val WINNER = TeamId(1)
private val LOSER = TeamId(2)
private val STRANGER = TeamId(3)
private val COMPLETION_SERIES_ID = SeriesId(60)

private class CompletionFixture {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway)

    fun series(
        completed: Boolean = false,
        completedAt: Instant? = null,
        reopenedAt: Instant? = null,
    ) = Series(
        id = COMPLETION_SERIES_ID,
        eventId = EventId(1),
        eventStage = EventStage.PLAYOFFS,
        totalGames = 3,
        participants = Pair(WINNER, LOSER),
        result = null,
        completed = completed,
        completedAt = completedAt,
        reopenedAt = reopenedAt,
    )

    fun withSeries(series: Series) {
        every { seriesRepo.getById(COMPLETION_SERIES_ID) } returns series
        every { seriesRepo.complete(any(), any(), any()) } returns series.copy(completed = true)
        every { seriesRepo.reopen(any(), any()) } returns
            series.copy(completed = false, completedAt = null, reopenedAt = Instant.now())
    }
}

class SeriesCompletionTest :
    StringSpec({
        "completing an open series sets the flags and records the result" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, LOSER)

            verify(exactly = 1) {
                f.seriesRepo.complete(COMPLETION_SERIES_ID, any(), SeriesResult(WINNER, LOSER))
            }
        }

        "a forfeit closes a series with zero games played" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, null)

            verify(exactly = 1) {
                f.seriesRepo.complete(COMPLETION_SERIES_ID, any(), SeriesResult(WINNER, LOSER))
            }
            verify(exactly = 0) { f.gameRepo.getBySeriesId(any()) }
        }

        "closing with no winner named sets the flags without writing a result" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.completeSeries(COMPLETION_SERIES_ID, null, null)

            verify(exactly = 1) { f.seriesRepo.complete(COMPLETION_SERIES_ID, any(), null) }
        }

        "completing an already-complete series is a conflict, not a silent overwrite" {
            val f = CompletionFixture()
            f.withSeries(f.series(completed = true, completedAt = Instant.parse("2026-07-14T23:12:04Z")))

            shouldThrow<IllegalStateException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, LOSER, WINNER)
            }

            verify(exactly = 0) { f.seriesRepo.complete(any(), any(), any()) }
        }

        "a winner outside the series is rejected on its own, not via the loser check" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, STRANGER, null)
            }

            verify(exactly = 0) { f.seriesRepo.complete(any(), any(), any()) }
        }

        "a winner outside the series is rejected when a loser is named too" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, STRANGER, LOSER)
            }
        }

        "a loser that contradicts the winner is rejected" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, WINNER)
            }
        }

        "a loser without a winner is rejected" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, null, LOSER)
            }
        }

        "completing an unknown series is not found" {
            val f = CompletionFixture()
            every { f.seriesRepo.getById(COMPLETION_SERIES_ID) } returns null

            shouldThrow<NoSuchElementException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, LOSER)
            }
        }

        "reopening a complete series stamps reopenedAt" {
            val f = CompletionFixture()
            f.withSeries(f.series(completed = true, completedAt = Instant.parse("2026-07-14T23:12:04Z")))
            val stamped = slot<Instant>()

            val reopened = f.service.reopenSeries(COMPLETION_SERIES_ID)

            verify(exactly = 1) { f.seriesRepo.reopen(COMPLETION_SERIES_ID, capture(stamped)) }
            stamped.isCaptured shouldBe true
            reopened.completed shouldBe false
        }

        "reopening a series that is not complete is a conflict" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalStateException> { f.service.reopenSeries(COMPLETION_SERIES_ID) }

            verify(exactly = 0) { f.seriesRepo.reopen(any(), any()) }
        }

        "reopening an unknown series is not found" {
            val f = CompletionFixture()
            every { f.seriesRepo.getById(COMPLETION_SERIES_ID) } returns null

            shouldThrow<NoSuchElementException> { f.service.reopenSeries(COMPLETION_SERIES_ID) }
        }

        "a reopened series is not closed again by a later result" {
            val f = CompletionFixture()
            val reopened = f.series(reopenedAt = Instant.parse("2026-07-15T09:00:00Z"))
            f.withSeries(reopened)

            f.service.evaluateCompletion(COMPLETION_SERIES_ID)

            verify(exactly = 0) { f.seriesRepo.complete(any(), any(), any()) }
            verify(exactly = 0) { f.gameRepo.getBySeriesId(any()) }
        }

        "manual completion still works on a reopened series" {
            val f = CompletionFixture()
            f.withSeries(f.series(reopenedAt = Instant.parse("2026-07-15T09:00:00Z")))

            f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, LOSER)

            verify(exactly = 1) {
                f.seriesRepo.complete(COMPLETION_SERIES_ID, any(), SeriesResult(WINNER, LOSER))
            }
        }
    })
