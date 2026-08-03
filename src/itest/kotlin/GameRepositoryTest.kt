import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.TournamentCodeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class GameRepositoryTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(
                    MountableFile.forClasspathResource("sql"),
                    "/docker-entrypoint-initdb.d/",
                )
            }
        val ds =
            install(JdbcDatabaseContainerSpecExtension(postgres)) {
                maximumPoolSize = 1
            }
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = GameRepository(dsl)
        val codeRepo = TournamentCodeRepository(dsl)
        lateinit var event: Event
        lateinit var team1: Team
        lateinit var team2: Team
        lateinit var seriesRepo: SeriesRepository

        beforeSpec {
            val e = EventRepository(dsl)
            event = e.insert(
                NewEvent(
                    name = "Test".toEventName(),
                    description = "Testing games.".toEventDescription(),
                    startDate = Instant.now(),
                    endDate = Instant.now().plusSeconds(3_600L),
                    status = EventStatus.ACTIVE,
                    eventStages = setOf(EventStage.REGULAR_SEASON),
                ),
                riotTournamentId = 1.toRiotTournamentId(),
            ) ?: throw Exception("Failed to insert initial event.")
            val t = TeamRepository(dsl)
            team1 = t.insert(NewTeam(name = "Team 1".toTeamName()))
                ?: throw Exception("Failed to insert team 1.")
            team2 = t.insert(NewTeam(name = "Team 2".toTeamName()))
                ?: throw Exception("Failed to insert team 2.")
            seriesRepo = SeriesRepository(dsl)
        }

        fun newSeries(): Series =
            seriesRepo.insert(
                NewSeries(
                    eventId = event.id,
                    totalGames = 5,
                    participantIds = Pair(team1.id, team2.id),
                    eventStage = EventStage.REGULAR_SEASON,
                ),
            ) ?: throw Exception("Failed to insert series.")

        fun issueCode(
            series: Series,
            shortcode: String,
        ) = codeRepo.insert(
            NewTournamentCode(seriesId = series.id, blueTeamId = team1.id, redTeamId = team2.id),
            shortcode = shortcode.toShortcode(),
        ) ?: throw Exception("Failed to issue code.")

        test("a bum code consumes no game number, so the next game is still 1 (TC4)") {
            val series = newSeries()
            issueCode(series, "TC4-BUM")
            issueCode(series, "TC4-REPLACEMENT")

            repo.countBySeries(series.id) shouldBe 0

            val game =
                repo.insert(
                    NewGame(
                        seriesId = series.id,
                        tournamentCodeId = null,
                        riotMatchId = null,
                        result = null,
                    ),
                )
            game.shouldNotBeNull()
            game.number shouldBe 1
        }

        test("game numbers reflect insert order (TC9)") {
            val series = newSeries()
            val first =
                repo
                    .insert(
                        NewGame(series.id, null, "NA1_1000000001", null),
                    ).shouldNotBeNull()
            val second =
                repo
                    .insert(
                        NewGame(series.id, null, "NA1_1000000002", null),
                    ).shouldNotBeNull()
            val third =
                repo
                    .insert(
                        NewGame(series.id, null, "NA1_1000000003", null),
                    ).shouldNotBeNull()

            first.number shouldBe 1
            second.number shouldBe 2
            third.number shouldBe 3
            repo.getBySeriesId(series.id).map { it.number } shouldBe listOf(1, 2, 3)
        }

        test("a codeless game still gets a number and belongs to its series") {
            val series = newSeries()

            val game = repo.insert(NewGame(series.id, null, null, null)).shouldNotBeNull()

            game.tournamentCodeId.shouldBeNull()
            game.riotMatchId.shouldBeNull()
            game.seriesId shouldBe series.id
            game.number shouldBe 1
        }

        test("a coded game keeps its tournament code and riot match id") {
            val series = newSeries()
            val code = issueCode(series, "CODED-1")

            val game =
                repo.insert(NewGame(series.id, code.id, "NA1_5102531894", null)).shouldNotBeNull()

            game.tournamentCodeId shouldBe code.id
            game.riotMatchId shouldBe "NA1_5102531894"
        }

        test("a duplicate riot match id is rejected") {
            val series = newSeries()
            repo.insert(NewGame(series.id, null, "NA1_DUPLICATE", null)).shouldNotBeNull()

            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(NewGame(series.id, null, "NA1_DUPLICATE", null))
            }
        }

        test("a game recorded with a result reads it back populated") {
            val series = newSeries()
            val result = GameResult(winningTeamId = team1.id, losingTeamId = team2.id)

            val game = repo.insert(NewGame(series.id, null, null, result)).shouldNotBeNull()

            game.result.shouldNotBeNull()
            game.result?.winningTeamId shouldBe team1.id
            game.result?.losingTeamId shouldBe team2.id
            repo.getById(game.id)?.result?.winningTeamId shouldBe team1.id
        }

        test("recordResult attaches a result to an existing game") {
            val series = newSeries()
            val game = repo.insert(NewGame(series.id, null, null, null)).shouldNotBeNull()
            game.result.shouldBeNull()

            val updated =
                repo.recordResult(game.id, GameResult(team2.id, team1.id)).shouldNotBeNull()

            updated.result?.winningTeamId shouldBe team2.id
            updated.number shouldBe game.number
        }

        test("counts and reads are scoped to a single series") {
            val a = newSeries()
            val b = newSeries()
            repo.insert(NewGame(a.id, null, null, null)).shouldNotBeNull()
            repo.insert(NewGame(a.id, null, null, null)).shouldNotBeNull()
            repo.insert(NewGame(b.id, null, null, null)).shouldNotBeNull()

            repo.countBySeries(a.id) shouldBe 2
            repo.countBySeries(b.id) shouldBe 1
            repo.getBySeriesId(b.id).single().number shouldBe 1
        }
    })
