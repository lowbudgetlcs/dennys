import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.TournamentCodeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.jooq.storage.tables.references.GAMES
import org.jooq.storage.tables.references.TOURNAMENT_CODES
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class TournamentCodeRepositoryTest :
    StringSpec({
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
        val repo = TournamentCodeRepository(dsl)
        lateinit var event: Event
        lateinit var team1: Team
        lateinit var team2: Team
        lateinit var series: Series
        lateinit var newCode: NewTournamentCode

        beforeSpec {
            val e = EventRepository(dsl)
            event = e.insert(
                NewEvent(
                    name = "Test".toEventName(),
                    description = "Testing series.".toEventDescription(),
                    startDate = Instant.now(),
                    endDate = Instant.now().plusSeconds(3_600L),
                    status = EventStatus.ACTIVE,
                    eventStages = setOf(EventStage.REGULAR_SEASON),
                ),
                riotTournamentId = 1.toRiotTournamentId(),
            ) ?: throw Exception("Failed to insert initial event.")
            val t = TeamRepository(dsl)
            team1 = t.insert(
                NewTeam(
                    name = "Team 1".toTeamName(),
                ),
            ) ?: throw Exception("Failed to insert team 1.")
            team2 = t.insert(
                NewTeam(
                    name = "Team 2".toTeamName(),
                ),
            ) ?: throw Exception("Failed to insert team 2.")
            val newSeries =
                NewSeries(
                    eventId = event.id,
                    totalGames = 5,
                    participantIds = Pair(team1.id, team2.id),
                    eventStage = EventStage.REGULAR_SEASON,
                )
            val seriesRepository = SeriesRepository(dsl)
            series = seriesRepository.insert(newSeries) ?: throw Exception("Failed to insert initial series.")
            newCode =
                NewTournamentCode(
                    seriesId = series.id,
                    blueTeamId = team1.id,
                    redTeamId = team2.id,
                )
        }

        "Issuing a tournament code stores it against the series" {
            val shortcode = "ABCD".toShortcode()
            val code =
                repo.insert(
                    newCode,
                    shortcode = shortcode,
                )
            code.shouldNotBeNull()
            code.shortcode shouldBe shortcode
            code.seriesId shouldBe series.id
            code.blueTeamId shouldBe team1.id
            code.redTeamId shouldBe team2.id
            code.createdAt.shouldNotBeNull()
        }

        "Issuing two codes creates two tournament_codes rows and zero games rows" {
            repo.insert(newCode, shortcode = "DCBA".toShortcode()).shouldNotBeNull()

            dsl.fetchCount(TOURNAMENT_CODES, TOURNAMENT_CODES.SERIES_ID.eq(series.id.value)) shouldBe 2
            dsl.fetchCount(GAMES, GAMES.SERIES_ID.eq(series.id.value)) shouldBe 0
        }

        "No game number is assigned at code-issue time" {
            val numberColumns =
                dsl.fetchCount(
                    DSL
                        .selectFrom("information_schema.columns")
                        .where("table_schema = 'dennys'")
                        .and("table_name = 'tournament_codes'")
                        .and("column_name = 'number'"),
                )
            numberColumns shouldBe 0

            val triggers = dsl.fetchCount(DSL.selectFrom("pg_trigger").where("tgname = 'set_game_number'"))
            triggers shouldBe 0
        }

        "Inserting a code with an invalid series id fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newCode.copy(seriesId = (-1).toSeriesId()), shortcode = "1234".toShortcode())
            }
        }

        "Inserting a code with a duplicate shortcode fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newCode, shortcode = "ABCD".toShortcode())
            }
        }
    })
