package services

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.SeriesService
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

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

    fun series(status: SeriesStatus = SeriesStatus.IN_PROGRESS) =
        Series(
            id = COMPLETION_SERIES_ID,
            eventId = EventId(1),
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(WINNER, LOSER),
            result = null,
            status = status,
        )

    fun withSeries(series: Series) {
        every { seriesRepo.getById(COMPLETION_SERIES_ID) } returns series
        every { seriesRepo.complete(any(), any()) } returns series.copy(status = SeriesStatus.COMPLETE)
        every { seriesRepo.reopen(any()) } returns series.copy(status = SeriesStatus.IN_PROGRESS)
    }
}

class SeriesCompletionTest :
    StringSpec({
        "completing an open series sets the flags and records the result" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, LOSER)

            verify(exactly = 1) {
                f.seriesRepo.complete(COMPLETION_SERIES_ID, SeriesResult(WINNER, LOSER))
            }
        }

        "a forfeit closes a series with zero games played" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, null)

            verify(exactly = 1) {
                f.seriesRepo.complete(COMPLETION_SERIES_ID, SeriesResult(WINNER, LOSER))
            }
            verify(exactly = 0) { f.gameRepo.getBySeriesId(any()) }
        }

        "closing with no winner named sets the flags without writing a result" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.completeSeries(COMPLETION_SERIES_ID, null, null)

            verify(exactly = 1) { f.seriesRepo.complete(COMPLETION_SERIES_ID, null) }
        }

        "completing an already-complete series is a conflict, not a silent overwrite" {
            val f = CompletionFixture()
            f.withSeries(f.series(SeriesStatus.COMPLETE))

            shouldThrow<IllegalStateException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, LOSER, WINNER)
            }

            verify(exactly = 0) { f.seriesRepo.complete(any(), any()) }
        }

        "a winner outside the series is rejected on its own, not via the loser check" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.completeSeries(COMPLETION_SERIES_ID, STRANGER, null)
            }

            verify(exactly = 0) { f.seriesRepo.complete(any(), any()) }
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

        "reopening a complete series moves it back to in progress" {
            val f = CompletionFixture()
            f.withSeries(f.series(SeriesStatus.COMPLETE))

            val reopened = f.service.reopenSeries(COMPLETION_SERIES_ID)

            verify(exactly = 1) { f.seriesRepo.reopen(COMPLETION_SERIES_ID) }
            reopened.status shouldBe SeriesStatus.IN_PROGRESS
        }

        "reopening a series that is not complete is a conflict" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalStateException> { f.service.reopenSeries(COMPLETION_SERIES_ID) }

            verify(exactly = 0) { f.seriesRepo.reopen(any()) }
        }

        "reopening an unknown series is not found" {
            val f = CompletionFixture()
            every { f.seriesRepo.getById(COMPLETION_SERIES_ID) } returns null

            shouldThrow<NoSuchElementException> { f.service.reopenSeries(COMPLETION_SERIES_ID) }
        }

        "a cancelled series is not closed by a later result" {
            val f = CompletionFixture()
            f.withSeries(f.series(SeriesStatus.CANCELLED))

            f.service.evaluateCompletion(COMPLETION_SERIES_ID)

            verify(exactly = 0) { f.seriesRepo.complete(any(), any()) }
            verify(exactly = 0) { f.gameRepo.getBySeriesId(any()) }
        }

        "manual completion works on a cancelled series" {
            val f = CompletionFixture()
            f.withSeries(f.series(SeriesStatus.CANCELLED))

            f.service.completeSeries(COMPLETION_SERIES_ID, WINNER, LOSER)

            verify(exactly = 1) {
                f.seriesRepo.complete(COMPLETION_SERIES_ID, SeriesResult(WINNER, LOSER))
            }
        }
    })
