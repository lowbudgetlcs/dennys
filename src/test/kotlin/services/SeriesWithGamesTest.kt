package services

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.Series
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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

private val BLUE = TeamId(1)
private val RED = TeamId(2)
private val VIEW_SERIES_ID = SeriesId(70)

private val FIRST_CODE_AT = Instant.parse("2026-07-14T22:00:00Z")
private val SECOND_CODE_AT = Instant.parse("2026-07-14T23:41:09Z")
private val FIRST_GAME_AT = Instant.parse("2026-07-14T22:30:00Z")
private val SECOND_GAME_AT = Instant.parse("2026-07-14T23:12:04Z")

private class ViewFixture {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway)

    val series =
        Series(
            id = VIEW_SERIES_ID,
            eventId = EventId(1),
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(BLUE, RED),
            result = null,
            completed = false,
            completedAt = null,
            reopenedAt = null,
        )

    fun code(
        id: Int,
        createdAt: Instant,
    ) = TournamentCode(
        id = TournamentCodeId(id),
        shortcode = Shortcode("SHORT-$id"),
        blueTeamId = BLUE,
        redTeamId = RED,
        seriesId = VIEW_SERIES_ID,
        createdAt = createdAt,
    )

    fun game(
        id: Int,
        codeId: Int?,
        number: Int,
        createdAt: Instant,
    ) = Game(
        id = GameId(id),
        seriesId = VIEW_SERIES_ID,
        tournamentCodeId = codeId?.let { TournamentCodeId(it) },
        riotMatchId = null,
        number = number,
        createdAt = createdAt,
        result = GameResult(BLUE, RED),
    )

    fun withContents(
        codes: List<TournamentCode>,
        games: List<Game>,
    ) {
        every { seriesRepo.getById(VIEW_SERIES_ID) } returns series
        every { codeRepo.getBySeriesId(VIEW_SERIES_ID) } returns codes
        every { gameRepo.getBySeriesId(VIEW_SERIES_ID) } returns games
    }
}

class SeriesWithGamesTest :
    StringSpec({
        "returns both collections and both timestamps" {
            val f = ViewFixture()
            f.withContents(
                codes = listOf(f.code(1, FIRST_CODE_AT), f.code(2, SECOND_CODE_AT)),
                games = listOf(f.game(10, 1, 1, FIRST_GAME_AT), f.game(11, 2, 2, SECOND_GAME_AT)),
            )

            val view = f.service.getSeriesWithGames(VIEW_SERIES_ID)

            view.tournamentCodes.map { it.id } shouldBe listOf(TournamentCodeId(1), TournamentCodeId(2))
            view.games.map { it.id } shouldBe listOf(GameId(10), GameId(11))
            view.lastCodeIssuedAt shouldBe SECOND_CODE_AT
            view.lastGameAt shouldBe SECOND_GAME_AT
        }

        "a bum code appears in tournamentCodes but not in games" {
            val f = ViewFixture()
            f.withContents(
                codes = listOf(f.code(1, FIRST_CODE_AT), f.code(2, SECOND_CODE_AT)),
                games = listOf(f.game(10, 1, 1, FIRST_GAME_AT)),
            )

            val view = f.service.getSeriesWithGames(VIEW_SERIES_ID)

            view.tournamentCodes.size shouldBe 2
            view.games.size shouldBe 1
            view.games.single().tournamentCodeId shouldBe TournamentCodeId(1)
        }

        "lastCodeIssuedAt reflects the newest code even when it is not last in the list" {
            val f = ViewFixture()
            f.withContents(
                codes = listOf(f.code(1, SECOND_CODE_AT), f.code(2, FIRST_CODE_AT)),
                games = emptyList(),
            )

            f.service.getSeriesWithGames(VIEW_SERIES_ID).lastCodeIssuedAt shouldBe SECOND_CODE_AT
        }

        "lastGameAt is null when no game has been recorded" {
            val f = ViewFixture()
            f.withContents(codes = listOf(f.code(1, FIRST_CODE_AT)), games = emptyList())

            val view = f.service.getSeriesWithGames(VIEW_SERIES_ID)

            view.lastGameAt.shouldBeNull()
            view.lastCodeIssuedAt shouldBe FIRST_CODE_AT
        }

        "both timestamps are null on a series with nothing issued or played" {
            val f = ViewFixture()
            f.withContents(codes = emptyList(), games = emptyList())

            val view = f.service.getSeriesWithGames(VIEW_SERIES_ID)

            view.lastCodeIssuedAt.shouldBeNull()
            view.lastGameAt.shouldBeNull()
        }

        "a codeless game is still counted as a game played" {
            val f = ViewFixture()
            f.withContents(
                codes = listOf(f.code(1, FIRST_CODE_AT)),
                games = listOf(f.game(10, null, 1, FIRST_GAME_AT)),
            )

            val view = f.service.getSeriesWithGames(VIEW_SERIES_ID)

            view.games.size shouldBe 1
            view.games.single().tournamentCodeId shouldBe null
            view.lastGameAt shouldBe FIRST_GAME_AT
        }

        "the series fields are carried through unchanged" {
            val f = ViewFixture()
            f.withContents(codes = emptyList(), games = emptyList())

            val view = f.service.getSeriesWithGames(VIEW_SERIES_ID)

            view.id shouldBe VIEW_SERIES_ID
            view.totalGames shouldBe 3
            view.eventStage shouldBe EventStage.PLAYOFFS
            view.participants shouldBe Pair(BLUE, RED)
            view.completed shouldBe false
        }

        "an unknown series id is not found" {
            val f = ViewFixture()
            every { f.seriesRepo.getById(VIEW_SERIES_ID) } returns null

            shouldThrow<NoSuchElementException> { f.service.getSeriesWithGames(VIEW_SERIES_ID) }
        }
    })
