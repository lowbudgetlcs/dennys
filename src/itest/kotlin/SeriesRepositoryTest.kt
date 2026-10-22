import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.filterByStatus
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.storage.tables.references.GAMES
import org.jooq.storage.tables.references.SERIES_RESULTS
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class SeriesRepositoryTest :
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
        val repo = SeriesRepository(dsl)
        lateinit var event: Event
        lateinit var team1: Team
        lateinit var team2: Team
        lateinit var newSeries: NewSeries

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

        test("insert and fetch series by id") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()

            val fetched = repo.getById(created.id)
            fetched shouldBe created
        }

        test("get all series by eventId, series have teamIds in output") {
            repeat(4) { repo.insert(newSeries) }
            val series = repo.getAllByEventId(event.id)
            series.shouldNotBeEmpty()
            series.shouldHaveSize(5)
        }

        test("a newly inserted series is not started") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()

            created.status shouldBe SeriesStatus.NOT_STARTED
        }

        test("complete sets the status to complete") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()

            val completed = repo.complete(created.id)
            completed.shouldNotBeNull()

            completed.status shouldBe SeriesStatus.COMPLETE
            repo.getById(created.id)?.status shouldBe SeriesStatus.COMPLETE
        }

        test("a forfeit writes series_results with no games played") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()

            val completed =
                repo.complete(created.id, SeriesResult(team1.id, team2.id))
            completed.shouldNotBeNull()

            completed.status shouldBe SeriesStatus.COMPLETE
            completed.result?.winningTeamId shouldBe team1.id
            completed.result?.losingTeamId shouldBe team2.id
            dsl.fetchCount(GAMES, GAMES.SERIES_ID.eq(created.id.value)) shouldBe 0
            dsl.fetchCount(SERIES_RESULTS, SERIES_RESULTS.SERIES_ID.eq(created.id.value)) shouldBe 1
        }

        test("completing twice overwrites the result rather than duplicating the row") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()
            repo.complete(created.id, SeriesResult(team1.id, team2.id))

            val corrected =
                repo.complete(created.id, SeriesResult(team2.id, team1.id))
            corrected.shouldNotBeNull()

            corrected.result?.winningTeamId shouldBe team2.id
            dsl.fetchCount(SERIES_RESULTS, SERIES_RESULTS.SERIES_ID.eq(created.id.value)) shouldBe 1
        }

        test("reopen moves the series back to in progress and preserves series_results") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()
            dsl
                .insertInto(SERIES_RESULTS)
                .set(SERIES_RESULTS.SERIES_ID, created.id.value)
                .set(SERIES_RESULTS.WINNER_TEAM_ID, team1.id.value)
                .set(SERIES_RESULTS.LOSER_TEAM_ID, team2.id.value)
                .execute()
            repo.complete(created.id)

            val reopened = repo.reopen(created.id)
            reopened.shouldNotBeNull()

            reopened.status shouldBe SeriesStatus.IN_PROGRESS
            reopened.result.shouldNotBeNull()
            reopened.result?.winningTeamId shouldBe team1.id
        }

        test("two series with the same pair and stage keep a stable id order across calls") {
            val e = EventRepository(dsl)
            val orderingEvent =
                e.insert(
                    NewEvent(
                        name = "Ordering".toEventName(),
                        description = "Two series, same pair, same stage.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.PLAYOFFS),
                    ),
                    riotTournamentId = 3.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert ordering event.")
            val repeatPair = newSeries.copy(eventId = orderingEvent.id, eventStage = EventStage.PLAYOFFS)
            val first = repo.insert(repeatPair) ?: throw Exception("Failed to insert first series.")
            val second = repo.insert(repeatPair) ?: throw Exception("Failed to insert second series.")
            val expected = listOf(first.id, second.id).sortedBy { it.value }

            repo.getAllByEventId(orderingEvent.id).map { it.id } shouldBe expected

            repo.complete(first.id)

            repo.getAllByEventId(orderingEvent.id).map { it.id } shouldBe expected
            repo.getAllByEventId(orderingEvent.id).map { it.id } shouldBe expected
        }

        test("status filtering splits two series that are otherwise identical") {
            val e = EventRepository(dsl)
            val filterEvent =
                e.insert(
                    NewEvent(
                        name = "Filtering".toEventName(),
                        description = "Two series, one closed.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.PLAYOFFS),
                    ),
                    riotTournamentId = 4.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert filtering event.")
            val repeatPair = newSeries.copy(eventId = filterEvent.id, eventStage = EventStage.PLAYOFFS)
            val open = repo.insert(repeatPair) ?: throw Exception("Failed to insert open series.")
            val closed = repo.insert(repeatPair) ?: throw Exception("Failed to insert series to close.")
            repo.complete(closed.id)
            val all = repo.getAllByEventId(filterEvent.id)

            all
                .filterByStatus(
                    SeriesQuery(teamIds = null, eventStage = null, status = SeriesStatus.NOT_STARTED),
                ).map { it.id } shouldBe listOf(open.id)

            all
                .filterByStatus(
                    SeriesQuery(teamIds = null, eventStage = null, status = SeriesStatus.COMPLETE),
                ).map { it.id } shouldBe listOf(closed.id)

            all
                .filterByStatus(
                    SeriesQuery(teamIds = null, eventStage = null),
                ).map { it.id } shouldBe listOf(open.id, closed.id)
        }
    })
