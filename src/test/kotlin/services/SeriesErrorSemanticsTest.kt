package services

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotShortcodeDto
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.ITournamentCodeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant

private val LEFT = TeamId(1)
private val RIGHT = TeamId(2)
private val ERR_SERIES_ID = SeriesId(80)
private val ERR_EVENT_ID = EventId(1)
private val ERR_AT = Instant.parse("2026-07-14T23:12:04Z")

private class ErrorFixture {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway)

    val series =
        Series(
            id = ERR_SERIES_ID,
            eventId = ERR_EVENT_ID,
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(LEFT, RIGHT),
            result = null,
            completed = false,
            completedAt = null,
            reopenedAt = null,
        )

    val event =
        Event(
            id = ERR_EVENT_ID,
            name = "Test".toEventName(),
            description = "Testing".toEventDescription(),
            riotTournamentId = 1.toRiotTournamentId(),
            createdAt = ERR_AT,
            startDate = ERR_AT,
            endDate = ERR_AT.plusSeconds(3_600L),
            status = EventStatus.ACTIVE,
            eventGroupId = null,
            eventStages = setOf(EventStage.PLAYOFFS),
        )

    fun code(id: Int) =
        TournamentCode(
            id = TournamentCodeId(id),
            shortcode = Shortcode("SHORT-$id"),
            blueTeamId = LEFT,
            redTeamId = RIGHT,
            seriesId = ERR_SERIES_ID,
            createdAt = ERR_AT,
        )

    fun game(id: Int) =
        Game(
            id = GameId(id),
            seriesId = ERR_SERIES_ID,
            tournamentCodeId = null,
            riotMatchId = null,
            number = 1,
            createdAt = ERR_AT,
            result = GameResult(LEFT, RIGHT),
        )

    fun withContents(
        codes: List<TournamentCode> = emptyList(),
        games: List<Game> = emptyList(),
    ) {
        every { seriesRepo.getById(ERR_SERIES_ID) } returns series
        every { codeRepo.getBySeriesId(ERR_SERIES_ID) } returns codes
        every { gameRepo.getBySeriesId(ERR_SERIES_ID) } returns games
        every { seriesRepo.delete(ERR_SERIES_ID) } returns Unit
    }

    fun readyToIssue() {
        every { seriesRepo.getById(ERR_SERIES_ID) } returns series
        every { codeRepo.getBySeriesId(ERR_SERIES_ID) } returns emptyList()
        every { gameRepo.getBySeriesId(ERR_SERIES_ID) } returns emptyList()
        every { teamRepo.getById(LEFT) } returns Team(LEFT, "Left".toTeamName(), null, ERR_EVENT_ID)
        every { teamRepo.getById(RIGHT) } returns Team(RIGHT, "Right".toTeamName(), null, ERR_EVENT_ID)
        every { eventRepo.getById(ERR_EVENT_ID) } returns event
    }

    val newCode = NewTournamentCode(ERR_SERIES_ID, LEFT, RIGHT)
}

class SeriesErrorSemanticsTest :
    StringSpec({
        "a Riot 429 is classified retryable" {
            RiotApiException("rate limited", 429).retryable shouldBe true
        }

        "a Riot 5xx is classified retryable" {
            RiotApiException("bad gateway", 502).retryable shouldBe true
            RiotApiException("server error", 500).retryable shouldBe true
        }

        "a Riot 4xx other than 429 is classified terminal" {
            RiotApiException("forbidden", 403).retryable shouldBe false
            RiotApiException("bad request", 400).retryable shouldBe false
            RiotApiException("not found", 404).retryable shouldBe false
        }

        "an unknown Riot status is treated as retryable rather than terminal" {
            RiotApiException("no status").retryable shouldBe true
        }

        "deleting a series holding tournament codes is a conflict" {
            val f = ErrorFixture()
            f.withContents(codes = listOf(f.code(1)))

            val ex = shouldThrow<IllegalStateException> { f.service.removeSeries(ERR_SERIES_ID) }

            ex.message.toString() shouldContain "Complete the series instead"
            verify(exactly = 0) { f.seriesRepo.delete(any()) }
        }

        "deleting a series holding games is a conflict even with no codes left" {
            val f = ErrorFixture()
            f.withContents(games = listOf(f.game(10)))

            shouldThrow<IllegalStateException> { f.service.removeSeries(ERR_SERIES_ID) }

            verify(exactly = 0) { f.seriesRepo.delete(any()) }
        }

        "deleting an empty series still works" {
            val f = ErrorFixture()
            f.withContents()

            f.service.removeSeries(ERR_SERIES_ID)

            verify(exactly = 1) { f.seriesRepo.delete(ERR_SERIES_ID) }
        }

        "deleting an unknown series is not found, not a conflict" {
            val f = ErrorFixture()
            every { f.seriesRepo.getById(ERR_SERIES_ID) } returns null

            shouldThrow<NoSuchElementException> { f.service.removeSeries(ERR_SERIES_ID) }
        }

        "a Riot failure while issuing a code propagates rather than collapsing to a gateway error" {
            val f = ErrorFixture()
            coEvery { f.gateway.getGames(any()) } returns emptyList()
            coEvery { f.gateway.getCode(any(), any()) } throws RiotApiException("rate limited", 429)
            f.readyToIssue()

            val ex = shouldThrow<RiotApiException> { f.service.createGame(f.newCode) }

            ex.status shouldBe 429
            ex.retryable shouldBe true
        }

        "Riot answering with no codes is a gateway error, not a not-found" {
            val f = ErrorFixture()
            coEvery { f.gateway.getGames(any()) } returns emptyList()
            coEvery { f.gateway.getCode(any(), any()) } returns RiotShortcodeDto(emptyList())
            f.readyToIssue()

            shouldThrow<GatewayException> { f.service.createGame(f.newCode) }

            verify(exactly = 0) { f.codeRepo.insert(any(), any()) }
        }
    })
