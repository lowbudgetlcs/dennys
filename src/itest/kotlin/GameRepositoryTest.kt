import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.game.models.NewGame
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class GameRepositoryTest :
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
        val repo = GameRepository(dsl)
        lateinit var event: Event
        lateinit var team1: Team
        lateinit var team2: Team
        lateinit var series: Series
        lateinit var newGame: NewGame

        beforeSpec {
            val e = EventRepository(dsl)
            event = e.insert(
                NewEvent(
                    name = "Test".toEventName(),
                    description = "Testing series.",
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
                    participantIds = listOf(team1.id, team2.id),
                    eventStage = EventStage.REGULAR_SEASON,
                )
            val seriesRepository = SeriesRepository(dsl)
            series = seriesRepository.insert(newSeries) ?: throw Exception("Failed to insert initial series.")
            newGame =
                NewGame(
                    seriesId = series.id,
                    blueTeamId = team1.id,
                    redTeamId = team2.id,
                )
        }

        "Inserting game succeeds and number = 1" {
            val shortcode = "ABCD".toShortcode()
            val game =
                repo.insert(
                    newGame,
                    shortcode = shortcode,
                )
            game.shouldNotBeNull()
            game.number shouldBe 1
            game.shortcode shouldBe shortcode
        }

        "Inserting game succeeds and number = 2" {
            val shortcode = "DCBA".toShortcode()
            val game =
                repo.insert(
                    newGame,
                    shortcode = shortcode,
                )
            game.shouldNotBeNull()
            game.number shouldBe 2
            game.shortcode shouldBe shortcode
        }

        "Inserting game with invalid series id fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newGame.copy(seriesId = (-1).toSeriesId()), shortcode = "1234".toShortcode())
            }
        }

        "Inserting game with duplicate shortcode fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newGame, shortcode = "ABCD".toShortcode())
            }
        }
    })
