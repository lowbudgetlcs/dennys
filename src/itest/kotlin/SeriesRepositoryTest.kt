import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class SeriesRepositoryTest :
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
        val repo = SeriesRepository(dsl)
        lateinit var event: Event
        lateinit var team1: Team
        lateinit var team2: Team
        lateinit var newSeries: NewSeries
        lateinit var createdSeries: Series

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
            newSeries =
                NewSeries(
                    eventId = event.id,
                    totalGames = 10,
                    participantIds = Pair(team1.id, team2.id),
                    eventStage = EventStage.REGULAR_SEASON,
                )
        }

        "insert and fetch series by id" {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()

            val fetched = repo.getById(created.id)
            fetched shouldBe created
            createdSeries = created
        }

        "get all series by eventId, series have teamIds in output" {
            repeat(4) { repo.insert(newSeries) }
            val series = repo.getAllByEventId(event.id)
            series.shouldNotBeEmpty()
            series.shouldHaveSize(5)
        }

        "insert series result" {
            val series = repo.getById(createdSeries.id)
            series.shouldNotBeNull()
            val result =
                SeriesResult(
                    seriesId = series.id,
                    winningTeamId = team1.id,
                    losingTeamId = team2.id,
                )
            val s = repo.insertSeriesResult(result)
            s.shouldNotBeNull()
            s.shouldBeEqualToIgnoringFields(series, Series::result)
            s.result.shouldNotBeNull()
            s.result!!.winningTeamId shouldBe team1.id
            s.result!!.losingTeamId shouldBe team2.id
        }
    })
