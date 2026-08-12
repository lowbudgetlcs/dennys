package services

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.DEFAULT_MAX_CODES_PER_GAME
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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

private val CAP_BLUE = TeamId(1)
private val CAP_RED = TeamId(2)
private val CAP_SERIES_ID = SeriesId(70)
private val CAP_EVENT_ID = EventId(1)
private val CAP_AT = Instant.parse("2026-08-11T12:00:00Z")

private class CapFixture(
    maxCodesPerGame: Int = DEFAULT_MAX_CODES_PER_GAME,
) {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway, maxCodesPerGame)

    val newCode = NewTournamentCode(CAP_SERIES_ID, CAP_BLUE, CAP_RED)

    private val series =
        Series(
            id = CAP_SERIES_ID,
            eventId = CAP_EVENT_ID,
            eventStage = EventStage.PLAYOFFS,
            totalGames = 5,
            participants = Pair(CAP_BLUE, CAP_RED),
            result = null,
            completed = false,
            completedAt = null,
            reopenedAt = null,
        )

    private val event =
        Event(
            id = CAP_EVENT_ID,
            name = "Test".toEventName(),
            description = "Testing".toEventDescription(),
            riotTournamentId = 1.toRiotTournamentId(),
            createdAt = CAP_AT,
            startDate = CAP_AT,
            endDate = CAP_AT.plusSeconds(3_600L),
            status = EventStatus.ACTIVE,
            eventGroupId = null,
            eventStages = setOf(EventStage.PLAYOFFS),
        )

    fun code(
        id: Int,
        createdAt: Instant,
    ) = TournamentCode(
        id = TournamentCodeId(id),
        shortcode = Shortcode("SHORT-$id"),
        blueTeamId = CAP_BLUE,
        redTeamId = CAP_RED,
        seriesId = CAP_SERIES_ID,
        createdAt = createdAt,
    )

    fun game(
        id: Int,
        createdAt: Instant,
        tournamentCodeId: TournamentCodeId? = null,
    ) = Game(
        id = GameId(id),
        seriesId = CAP_SERIES_ID,
        tournamentCodeId = tournamentCodeId,
        riotMatchId = null,
        number = id,
        createdAt = createdAt,
        result = GameResult(CAP_BLUE, CAP_RED),
    )

    /** Wires a series that is ready to issue, holding [codes] and [games]. */
    fun holding(
        codes: List<TournamentCode> = emptyList(),
        games: List<Game> = emptyList(),
    ) {
        every { seriesRepo.getById(CAP_SERIES_ID) } returns series
        every { codeRepo.getBySeriesId(CAP_SERIES_ID) } returns codes
        every { gameRepo.getBySeriesId(CAP_SERIES_ID) } returns games
        every { teamRepo.getById(CAP_BLUE) } returns Team(CAP_BLUE, "Blue".toTeamName(), null, CAP_EVENT_ID)
        every { teamRepo.getById(CAP_RED) } returns Team(CAP_RED, "Red".toTeamName(), null, CAP_EVENT_ID)
        every { eventRepo.getById(CAP_EVENT_ID) } returns event
        coEvery { gateway.getGames(any()) } returns emptyList()
        coEvery { gateway.getCode(any(), any()) } returns RiotShortcodeDto(listOf("SHORT-NEW"))
        every { codeRepo.insert(any(), any()) } returns code(99, CAP_AT.plusSeconds(999))
    }

    fun at(seconds: Long): Instant = CAP_AT.plusSeconds(seconds)
}

class SeriesCodeCapTest :
    StringSpec({
        "a code is issued while the series is under the limit" {
            val f = CapFixture()
            f.holding(codes = listOf(f.code(1, f.at(10))))

            f.service.createGame(f.newCode).seriesId shouldBe CAP_SERIES_ID

            coVerify(exactly = 1) { f.gateway.getCode(any(), any()) }
        }

        "the third code for one game is refused" {
            val f = CapFixture()
            f.holding(codes = listOf(f.code(1, f.at(10)), f.code(2, f.at(20))))

            val ex = shouldThrow<IllegalStateException> { f.service.createGame(f.newCode) }

            ex.message.toString() shouldContain "already been issued 2 tournament code(s) for this game"
        }

        "a refused request never reaches Riot" {
            val f = CapFixture()
            f.holding(codes = listOf(f.code(1, f.at(10)), f.code(2, f.at(20))))

            shouldThrow<IllegalStateException> { f.service.createGame(f.newCode) }

            coVerify(exactly = 0) { f.gateway.getCode(any(), any()) }
        }

        "the refusal names both remedies" {
            val f = CapFixture()
            f.holding(codes = listOf(f.code(1, f.at(10)), f.code(2, f.at(20))))

            val ex = shouldThrow<IllegalStateException> { f.service.createGame(f.newCode) }

            ex.message.toString() shouldContain "Report the result"
            ex.message.toString() shouldContain "custom game"
        }

        "recording a game grants a fresh allowance" {
            val f = CapFixture()
            f.holding(
                codes = listOf(f.code(1, f.at(10)), f.code(2, f.at(20))),
                games = listOf(f.game(1, f.at(40), TournamentCodeId(2))),
            )

            f.service.createGame(f.newCode)

            coVerify(exactly = 1) { f.gateway.getCode(any(), any()) }
        }

        "a codeless game from a custom resets the allowance just as a coded one does" {
            val f = CapFixture()
            f.holding(
                codes = listOf(f.code(1, f.at(10)), f.code(2, f.at(20))),
                games = listOf(f.game(1, f.at(40), tournamentCodeId = null)),
            )

            f.service.createGame(f.newCode)

            coVerify(exactly = 1) { f.gateway.getCode(any(), any()) }
        }

        "bum codes from an earlier game do not count against the current one" {
            val f = CapFixture()
            f.holding(
                codes =
                    listOf(
                        f.code(1, f.at(10)),
                        f.code(2, f.at(20)),
                        f.code(3, f.at(50)),
                    ),
                games = listOf(f.game(1, f.at(40), TournamentCodeId(2))),
            )

            f.service.createGame(f.newCode)

            coVerify(exactly = 1) { f.gateway.getCode(any(), any()) }
        }

        "the allowance counts only codes issued since the most recent game" {
            val f = CapFixture()
            f.holding(
                codes =
                    listOf(
                        f.code(1, f.at(10)),
                        f.code(4, f.at(50)),
                        f.code(5, f.at(60)),
                    ),
                games = listOf(f.game(1, f.at(40), TournamentCodeId(1))),
            )

            shouldThrow<IllegalStateException> { f.service.createGame(f.newCode) }

            coVerify(exactly = 0) { f.gateway.getCode(any(), any()) }
        }

        "the limit is configurable" {
            val f = CapFixture(maxCodesPerGame = 1)
            f.holding(codes = listOf(f.code(1, f.at(10))))

            shouldThrow<IllegalStateException> { f.service.createGame(f.newCode) }

            coVerify(exactly = 0) { f.gateway.getCode(any(), any()) }
        }
    })
