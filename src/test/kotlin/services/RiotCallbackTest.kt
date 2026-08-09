package services

import com.lowbudgetlcs.api.dto.riot.PostMatchDto
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
import io.kotest.matchers.nulls.shouldBeNull
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
import java.time.Instant

private val CB_BLUE = TeamId(1)
private val CB_RED = TeamId(2)
private val CB_SERIES_ID = SeriesId(90)
private val CB_EVENT_ID = EventId(1)
private val CB_AT = Instant.parse("2026-07-14T23:12:04Z")

private const val CB_PUUID = "aa-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
private const val CB_SHORTCODE = "NA04eff-c5b0b774-c049-47cf-88f4-eee05c8d83ae"

/** The body relay forwards byte-for-byte from Riot, per lowbudgetlcs/relay main.go RiotPayload. */
private const val RELAY_PAYLOAD = """
{
  "startTime": 1752534724000,
  "shortCode": "$CB_SHORTCODE",
  "metaData": "{\"tag\":\"LBLCS\",\"seriesId\":90}",
  "gameId": 5102531894,
  "gameName": "cb0b0b1e-0000-0000-0000-000000000000",
  "gameType": "Practice",
  "gameMap": 11,
  "gameMode": "CLASSIC",
  "region": "NA1"
}
"""

private val callbackJson = Json { ignoreUnknownKeys = true }

private class CallbackFixture {
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
            createdAt = CB_AT,
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

    fun series(completed: Boolean = false) =
        Series(
            id = CB_SERIES_ID,
            eventId = CB_EVENT_ID,
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(CB_BLUE, CB_RED),
            result = null,
            completed = completed,
            completedAt = if (completed) CB_AT else null,
            reopenedAt = null,
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
        every { codeRepo.getByShortcode(Shortcode(CB_SHORTCODE)) } returns code
        every { codeRepo.getBySeriesId(CB_SERIES_ID) } returns listOf(code)
        every { gameRepo.getBySeriesId(CB_SERIES_ID) } answers { games.toList() }
        every { teamRepo.getTeamIdsByPuuids(any()) } returns listOf(CB_BLUE)
        every { seriesRepo.complete(any(), any(), any()) } returns series.copy(completed = true)
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

class RiotCallbackTest :
    StringSpec({
        "the payload relay forwards deserializes into PostMatchDto unchanged" {
            val dto = callbackJson.decodeFromString<PostMatchDto>(RELAY_PAYLOAD)

            dto.shortCode shouldBe CB_SHORTCODE
            dto.gameId shouldBe 5102531894L
            dto.region shouldBe "NA1"
            dto.gameMap shouldBe 11
            dto.startTime shouldBe 1752534724000L
        }

        "the metadata Denny's stamps carries the tag relay requires to forward at all" {
            val f = CallbackFixture()
            val options = slot<ShortcodeOptions>()
            coEvery { f.gateway.getGames(any()) } returns emptyList()
            coEvery { f.gateway.getCode(any(), capture(options)) } returns RiotShortcodeDto(listOf(CB_SHORTCODE))
            f.withSeries()
            every { f.teamRepo.getById(CB_BLUE) } returns Team(CB_BLUE, "Blue".toTeamName(), null, CB_EVENT_ID)
            every { f.teamRepo.getById(CB_RED) } returns Team(CB_RED, "Red".toTeamName(), null, CB_EVENT_ID)
            every { f.eventRepo.getById(CB_EVENT_ID) } returns f.event
            every { f.codeRepo.insert(any(), any()) } returns f.code

            f.service.createGame(NewTournamentCode(CB_SERIES_ID, CB_BLUE, CB_RED))

            val metadata = Json.parseToJsonElement(options.captured.metadata).jsonObject
            metadata["tag"]?.jsonPrimitive?.content shouldBe "LBLCS"
            metadata["seriesId"]?.jsonPrimitive?.content shouldBe "90"
        }

        "a callback records the game and its result" {
            val f = CallbackFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()

            f.service.refreshFromShortcode(Shortcode(CB_SHORTCODE)) shouldBe RefreshOutcome.ATTRIBUTED

            f.games.size shouldBe 1
            f.games.single().tournamentCodeId shouldBe TournamentCodeId(1)
            f.games.single().riotMatchId shouldBe "NA1_5102531894".toRiotMatchId()
            f.games.single().result shouldBe GameResult(CB_BLUE, CB_RED)
        }

        "the same callback twice records one game" {
            val f = CallbackFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()

            f.service.refreshFromShortcode(Shortcode(CB_SHORTCODE))
            f.service.refreshFromShortcode(Shortcode(CB_SHORTCODE)) shouldBe RefreshOutcome.ANSWERED_EMPTY

            f.games.size shouldBe 1
        }

        "a report followed by the callback for that code records one game" {
            val f = CallbackFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries(recorded = listOf(f.recordedGame(codeId = 1)))

            f.service.refreshFromShortcode(Shortcode(CB_SHORTCODE)) shouldBe RefreshOutcome.ANSWERED_EMPTY

            f.games.size shouldBe 1
            coVerify(exactly = 0) { f.gateway.getGames(any()) }
        }

        "the callback followed by the same report records one game" {
            val f = CallbackFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries()

            f.service.refreshFromShortcode(Shortcode(CB_SHORTCODE))
            val outcome = f.service.reportResult(CB_SERIES_ID, ReportedResult(null, null, null, null))

            outcome.recorded shouldBe false
            f.games.size shouldBe 1
        }

        "a late callback for a closed series records the game and leaves it closed" {
            val f = CallbackFixture()
            coEvery { f.gateway.getGames(Shortcode(CB_SHORTCODE)) } returns listOf(f.riotGame())
            f.withSeries(series = f.series(completed = true))

            f.service.refreshFromShortcode(Shortcode(CB_SHORTCODE)) shouldBe RefreshOutcome.ATTRIBUTED

            f.games.size shouldBe 1
            verify(exactly = 0) { f.seriesRepo.complete(any(), any(), any()) }
        }

        "a callback for an unknown shortcode writes nothing" {
            val f = CallbackFixture()
            every { f.codeRepo.getByShortcode(Shortcode("NOT-OURS")) } returns null

            f.service.refreshFromShortcode(Shortcode("NOT-OURS")).shouldBeNull()

            verify(exactly = 0) { f.gameRepo.insert(any()) }
            coVerify(exactly = 0) { f.gateway.getGames(any()) }
        }
    })
