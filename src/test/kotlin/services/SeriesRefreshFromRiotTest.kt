package services

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.RefreshOutcome
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
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
import io.mockk.verify
import java.time.Instant

private val TEAM_1 = TeamId(1)
private val TEAM_2 = TeamId(2)
private val SERIES_ID = SeriesId(50)

private const val PUUID_A = "aa-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
private const val PUUID_B = "bb-bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
private const val PUUID_C = "cc-ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"

private class Fixture {
    val codeRepo = mockk<ITournamentCodeRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val eventRepo = mockk<IEventRepository>(relaxed = false)
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val gameRepo = mockk<IGameRepository>(relaxed = false)
    val gateway = mockk<IRiotTournamentGateway>(relaxed = false)
    val service = SeriesService(codeRepo, seriesRepo, eventRepo, teamRepo, gameRepo, gateway)

    val series =
        Series(
            id = SERIES_ID,
            eventId = EventId(1),
            eventStage = EventStage.PLAYOFFS,
            totalGames = 3,
            participants = Pair(TEAM_1, TEAM_2),
            result = null,
            completed = false,
            completedAt = null,
            reopenedAt = null,
        )

    fun code(
        id: Int,
        shortcode: String,
    ) = TournamentCode(
        id = TournamentCodeId(id),
        shortcode = Shortcode(shortcode),
        blueTeamId = TEAM_1,
        redTeamId = TEAM_2,
        seriesId = SERIES_ID,
        createdAt = Instant.parse("2026-07-14T22:58:31Z"),
    )

    fun riotGame(
        winners: List<String>,
        region: String = "NA",
        shortCode: String = "SHORT-A",
    ) = RiotTournamentGamesV5Dto(
        winningTeam = winners.map { RiotTournamentTeamV5Dto(it) },
        shortCode = shortCode,
        gameId = 5102531894L,
        region = region,
    )

    fun playedGame(codeId: Int) =
        Game(
            id = GameId(1),
            seriesId = SERIES_ID,
            tournamentCodeId = TournamentCodeId(codeId),
            riotMatchId = null,
            number = 1,
            createdAt = Instant.parse("2026-07-14T23:12:04Z"),
            result = null,
        )

    fun withSeries(
        codes: List<TournamentCode>,
        recorded: List<Game> = emptyList(),
    ) {
        every { seriesRepo.getById(SERIES_ID) } returns series
        every { gameRepo.getBySeriesId(SERIES_ID) } returns recorded
        every { codeRepo.getBySeriesId(SERIES_ID) } returns codes
        every { gameRepo.insert(any()) } returns null
        every { seriesRepo.complete(any(), any(), any()) } returns series
    }
}

class SeriesRefreshFromRiotTest :
    StringSpec({
        "attributes a game Riot has and reports ATTRIBUTED" {
            val f = Fixture()
            coEvery { f.gateway.getGames(Shortcode("SHORT-A")) } returns
                listOf(f.riotGame(listOf(PUUID_A, PUUID_B)))
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.teamRepo.getTeamIdsByPuuids(any()) } returns listOf(TEAM_1, TEAM_1)

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ATTRIBUTED

            verify(exactly = 1) {
                f.gameRepo.insert(
                    NewGame(
                        seriesId = SERIES_ID,
                        tournamentCodeId = TournamentCodeId(1),
                        riotMatchId = "NA1_5102531894".toRiotMatchId(),
                        result = GameResult(TEAM_1, TEAM_2),
                    ),
                )
            }
        }

        "reports ANSWERED_EMPTY when Riot has no game" {
            val f = Fixture()
            coEvery { f.gateway.getGames(any()) } returns emptyList()
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ANSWERED_EMPTY

            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "reports UNREACHABLE distinctly from ANSWERED_EMPTY" {
            val f = Fixture()
            coEvery { f.gateway.getGames(any()) } throws RiotApiException("503")
            f.withSeries(listOf(f.code(1, "SHORT-A")))

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.UNREACHABLE

            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "attributes to the older code when the game was played on it" {
            val f = Fixture()
            val older = f.code(1, "SHORT-OLDER")
            val newer = f.code(2, "SHORT-NEWER")
            coEvery { f.gateway.getGames(older.shortcode) } returns
                listOf(f.riotGame(listOf(PUUID_A), shortCode = "SHORT-OLDER"))
            coEvery { f.gateway.getGames(newer.shortcode) } returns emptyList()
            f.withSeries(listOf(older, newer))
            every { f.teamRepo.getTeamIdsByPuuids(any()) } returns listOf(TEAM_1)

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ATTRIBUTED

            verify(exactly = 1) { f.gameRepo.insert(match { it.tournamentCodeId == TournamentCodeId(1) }) }
            verify(exactly = 0) { f.gameRepo.insert(match { it.tournamentCodeId == TournamentCodeId(2) }) }
        }

        "skips codes that already have a game" {
            val f = Fixture()
            f.withSeries(listOf(f.code(1, "SHORT-A")), recorded = listOf(f.playedGame(1)))

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ANSWERED_EMPTY

            coVerify(exactly = 0) { f.gateway.getGames(any()) }
        }

        "a match already recorded in the series is skipped rather than recorded twice" {
            val f = Fixture()
            coEvery { f.gateway.getGames(any()) } returns listOf(f.riotGame(listOf(PUUID_A)))
            val alreadyRecorded =
                f.playedGame(1).copy(riotMatchId = "NA1_5102531894".toRiotMatchId())
            f.withSeries(listOf(f.code(2, "SHORT-B")), recorded = listOf(alreadyRecorded))
            every { f.teamRepo.getTeamIdsByPuuids(any()) } returns listOf(TEAM_1)

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ANSWERED_EMPTY

            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "a winning roster with unregistered puuids still resolves via overlap" {
            val f = Fixture()
            coEvery { f.gateway.getGames(any()) } returns
                listOf(f.riotGame(listOf(PUUID_A, PUUID_B, PUUID_C)))
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.teamRepo.getTeamIdsByPuuids(any()) } returns listOf(TEAM_2, TEAM_2)

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ATTRIBUTED

            verify(exactly = 1) { f.gameRepo.insert(match { it.result == GameResult(TEAM_2, TEAM_1) }) }
        }

        "an all-unknown winning roster records nothing" {
            val f = Fixture()
            coEvery { f.gateway.getGames(any()) } returns listOf(f.riotGame(listOf(PUUID_A)))
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.teamRepo.getTeamIdsByPuuids(any()) } returns emptyList()

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ANSWERED_EMPTY

            verify(exactly = 0) { f.gameRepo.insert(any()) }
        }

        "an unknown region leaves riotMatchId null rather than failing" {
            val f = Fixture()
            coEvery { f.gateway.getGames(any()) } returns
                listOf(f.riotGame(listOf(PUUID_A), region = "NOPE"))
            f.withSeries(listOf(f.code(1, "SHORT-A")))
            every { f.teamRepo.getTeamIdsByPuuids(any()) } returns listOf(TEAM_1)

            f.service.refreshFromRiot(SERIES_ID) shouldBe RefreshOutcome.ATTRIBUTED

            verify(exactly = 1) { f.gameRepo.insert(match { it.riotMatchId == null }) }
        }
    })
