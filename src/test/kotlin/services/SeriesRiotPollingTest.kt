package services

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.RefreshOutcome
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotShortcodeDto
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGamesV5Dto
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentTeamV5Dto
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.ITournamentCodeRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Duration
import java.time.Instant

private val CB_BLUE = TeamId(1)
private val CB_RED = TeamId(2)
private val CB_SERIES_ID = SeriesId(90)
private val CB_EVENT_ID = EventId(1)
private val CB_AT = Instant.parse("2026-07-14T23:12:04Z")

private const val CB_PUUID = "aa-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
private const val CB_SHORTCODE = "NA04eff-c5b0b774-c049-47cf-88f4-eee05c8d83ae"

private class PollFixture {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway)

    val games = mutableListOf<Game>()
    private var nextId = 200

    val code =
        TournamentCode(
            id = TournamentCodeId(1),
            shortcode = Shortcode(CB_SHORTCODE),
            blueTeamId = CB_BLUE,
            redTeamId = CB_RED,
            seriesId = CB_SERIES_ID,
            createdAt = Instant.now().minus(Duration.ofHours(1)),
        )

    val event =
        Event(
            id = CB_EVENT_ID,
            name = "Test".toEventName(),
            description = "Testing".toEventDescription(),
            riotTournamentId = 1.toRiotTournamentId(),
            createdAt = CB_AT,
            startDate = CB_AT,
            endDate = CB_AT.plusSeconds(3_600L),
            status = EventStatus.ACTIVE,
            eventGroupId = null,
            eventStages = setOf(EventStage.PLAYOFFS),
        )

    fun series(status: SeriesStatus = SeriesStatus.NOT_STARTED) =
        Series(
            id = CB_SERIES_ID,
            eventId = CB_EVENT_ID,
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(CB_BLUE, CB_RED),
            result = null,
            status = status,
        )

    fun riotGame() =
        RiotTournamentGamesV5Dto(
            winningTeam = listOf(RiotTournamentTeamV5Dto(CB_PUUID)),
            shortCode = CB_SHORTCODE,
            gameId = 5102531894L,
            region = "NA",
        )

    fun recordedGame(codeId: Int?) =
        Game(
            id = GameId(nextId++),
            seriesId = CB_SERIES_ID,
            tournamentCodeId = codeId?.let { TournamentCodeId(it) },
            riotMatchId = "NA1_5102531894".toRiotMatchId(),
            number = games.size + 1,
            createdAt = CB_AT,
            result = GameResult(CB_BLUE, CB_RED),
        )

    fun withSeries(
        series: Series = series(),
        recorded: List<Game> = emptyList(),
    ) {
        games.clear()
        games += recorded
        every { seriesRepo.getById(CB_SERIES_ID) } returns series
        every { seriesRepo.markInProgress(any()) } returns Unit
        every { codeRepo.getByShortcode(Shortcode(CB_SHORTCODE)) } returns code
        every { codeRepo.getById(code.id) } returns code
        every { codeRepo.getOpenForPolling(any()) } returns listOf(code)
        every { codeRepo.getBySeriesId(CB_SERIES_ID) } returns listOf(code)
        every { gameRepo.getBySeriesId(CB_SERIES_ID) } answers { games.toList() }
        every { teamRepo.getTeamIdsByPuuids(any()) } returns listOf(CB_BLUE)
        every { seriesRepo.setStatus(any(), any(), any(), any()) } returns series.copy(status = SeriesStatus.COMPLETE)
        every { gameRepo.insert(any()) } answers {
            val new = firstArg<NewGame>()
            val game =
                Game(
                    id = GameId(nextId++),
                    seriesId = new.seriesId,
                    tournamentCodeId = new.tournamentCodeId,
                    riotMatchId = new.riotMatchId,
                    number = games.size + 1,
                    createdAt = CB_AT,
                    result = new.result,
                )
            games += game
            game
        }
    }
}

class SeriesRiotPollingTest :
    StringSpec({
        "the metadata Denny's stamps carries the tag and series id" {
            val f = PollFixture()
            val options = slot<ShortcodeOptions>()
            coEvery { f.gateway.getGames(any()) } returns emptyList()
            coEvery { f.gateway.getCode(any(), capture(options)) } returns RiotShortcodeDto(listOf(CB_SHORTCODE))
            f.withSeries()
            every { f.teamRepo.getById(CB_BLUE) } returns Team(CB_BLUE, "Blue".toTeamName(), null, CB_EVENT_ID)
            every { f.teamRepo.getById(CB_RED) } returns Team(CB_RED, "Red".toTeamName(), null, CB_EVENT_ID)
            every { f.eventRepo.getById(CB_EVENT_ID) } returns f.event
            every { f.codeRepo.insert(any(), any()) } returns f.code

            f.service.createGame(NewTournamentCode(CB_SERIES_ID, CB_BLUE, CB_RED), forceNew = true)

            val metadata = Json.parseToJsonElement(options.captured.metadata).jsonObject
            metadata["tag"]?.jsonPrimitive?.content shouldBe "LBLCS"
            metadata["seriesId"]?.jsonPrimitive?.content shouldBe "90"
        }

        "a poll records the game and its result" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()

            f.service.pollRiot(includeStale = false)

            f.games.size shouldBe 1
            f.games.single().tournamentCodeId shouldBe TournamentCodeId(1)
            f.games.single().riotMatchId shouldBe "NA1_5102531894".toRiotMatchId()
            f.games.single().result shouldBe GameResult(CB_BLUE, CB_RED)
        }

        "a poll that records the deciding game closes the series" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries(recorded = listOf(f.recordedGame(codeId = null).copy(riotMatchId = null)))

            f.service.pollRiot(includeStale = false)

            verify(exactly = 1) { f.seriesRepo.setStatus(CB_SERIES_ID, SeriesStatus.COMPLETE, any(), false) }
        }

        "polling twice records one game" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()

            f.service.pollRiot(includeStale = false)
            f.service.pollRiot(includeStale = false)

            f.games.size shouldBe 1
        }

        "a code older than six hours is polled only when stale codes are included" {
            val f = PollFixture()
            val stale = f.code.copy(createdAt = Instant.now().minus(Duration.ofHours(7)))
            coEvery { f.gateway.getGames(any()) } returns emptyList()
            f.withSeries()
            every { f.codeRepo.getOpenForPolling(any()) } returns listOf(stale)
            every { f.codeRepo.getById(stale.id) } returns stale

            f.service.pollRiot(includeStale = false)
            coVerify(exactly = 0) { f.gateway.getGames(any()) }

            f.service.pollRiot(includeStale = true)
            coVerify(exactly = 1) { f.gateway.getGames(Shortcode(CB_SHORTCODE)) }
        }

        "a code that fails does not stop the rest of the poll" {
            val f = PollFixture()
            val broken = f.code.copy(id = TournamentCodeId(2), shortcode = Shortcode("BROKEN"))
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()
            every { f.codeRepo.getOpenForPolling(any()) } returns listOf(broken, f.code)
            every { f.codeRepo.getById(broken.id) } throws IllegalStateException("boom")

            f.service.pollRiot(includeStale = false)

            f.games.size shouldBe 1
        }

        "a report followed by a poll for that code records one game" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries(recorded = listOf(f.recordedGame(codeId = 1).copy(riotMatchId = null)))

            f.service.pollRiot(includeStale = false)

            f.games.size shouldBe 1
            coVerify(exactly = 0) { f.gateway.getGames(any()) }
        }

        "a poll followed by the same report records one game" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()

            f.service.pollRiot(includeStale = false)
            val report = ReportedResult(CB_BLUE, null, null, Shortcode(CB_SHORTCODE))
            val outcome = f.service.reportResult(CB_SERIES_ID, report)

            outcome.recorded shouldBe false
            f.games.size shouldBe 1
        }

        "a late Riot result for a closed series records the game and leaves it closed" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries(series = f.series(SeriesStatus.COMPLETE))

            f.service.refreshFromCode(CB_SERIES_ID, f.code.id, null) shouldBe RefreshOutcome.ATTRIBUTED

            f.games.size shouldBe 1
            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
        }

        "a late Riot result for a double-loss series records the game and keeps the double loss" {
            val f = PollFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries(series = f.series(SeriesStatus.COMPLETE).copy(doubleLoss = true))

            f.service.refreshFromCode(CB_SERIES_ID, f.code.id, null) shouldBe RefreshOutcome.ATTRIBUTED

            f.games.size shouldBe 1
            verify(exactly = 0) { f.seriesRepo.setStatus(any(), any(), any(), any()) }
        }
    })
