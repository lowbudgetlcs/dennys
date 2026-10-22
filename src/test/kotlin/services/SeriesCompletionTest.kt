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
        every { seriesRepo.setStatus(any(), any(), any(), any()) } returns series.copy(status = SeriesStatus.COMPLETE)
    }
}

class SeriesCompletionTest :
    StringSpec({
        "patching to complete with a winner records the result" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, WINNER, false)

            verify(exactly = 1) {
                f.seriesRepo.setStatus(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, SeriesResult(WINNER, LOSER), false)
            }
            verify(exactly = 0) { f.gameRepo.getBySeriesId(any()) }
        }

        "a double loss records no winner" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, null, true)

            verify(exactly = 1) { f.seriesRepo.setStatus(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, null, true) }
        }

        "a winner and a double loss together are rejected" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, WINNER, true)
            }

            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
        }

        "a winner or double loss on a status other than complete is rejected" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.IN_PROGRESS, WINNER, false)
            }
            shouldThrow<IllegalArgumentException> {
                f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.CANCELLED, null, true)
            }

            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
        }

        "a winner outside the series is rejected" {
            val f = CompletionFixture()
            f.withSeries(f.series())

            shouldThrow<IllegalArgumentException> {
                f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, STRANGER, false)
            }

            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
        }

        "a complete series can be patched again to correct its result" {
            val f = CompletionFixture()
            f.withSeries(f.series(SeriesStatus.COMPLETE))

            f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, LOSER, false)

            verify(exactly = 1) {
                f.seriesRepo.setStatus(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, SeriesResult(LOSER, WINNER), false)
            }
        }

        "patching an unknown series is not found" {
            val f = CompletionFixture()
            every { f.seriesRepo.getById(COMPLETION_SERIES_ID) } returns null

            shouldThrow<NoSuchElementException> {
                f.service.patchSeries(COMPLETION_SERIES_ID, SeriesStatus.COMPLETE, WINNER, false)
            }
        }

        "a cancelled series is not closed by a later result" {
            val f = CompletionFixture()
            f.withSeries(f.series(SeriesStatus.CANCELLED))

            f.service.evaluateCompletion(COMPLETION_SERIES_ID)

            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
            verify(exactly = 0) { f.gameRepo.getBySeriesId(any()) }
        }

        "a series cannot hold both a winner and a double loss" {
            shouldThrow<IllegalArgumentException> {
                CompletionFixture().series().copy(result = SeriesResult(WINNER, LOSER), doubleLoss = true)
            }
        }
    })
