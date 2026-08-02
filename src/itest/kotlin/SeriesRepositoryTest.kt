import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.filterByCompletion
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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
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

        test("a newly inserted series is not completed") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()

            created.completed shouldBe false
            created.completedAt.shouldBeNull()
            created.reopenedAt.shouldBeNull()
        }

        test("complete sets the flag and stamps completedAt") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()
            val at = Instant.parse("2026-07-14T23:12:04Z")

            val completed = repo.complete(created.id, at)
            completed.shouldNotBeNull()

            completed.completed shouldBe true
            completed.completedAt shouldBe at
            completed.reopenedAt.shouldBeNull()
            repo.getById(created.id)?.completed shouldBe true
        }

        test("reopen clears completion, stamps reopenedAt and preserves series_results") {
            val created = repo.insert(newSeries)
            created.shouldNotBeNull()
            dsl
                .insertInto(SERIES_RESULTS)
                .set(SERIES_RESULTS.SERIES_ID, created.id.value)
                .set(SERIES_RESULTS.WINNER_TEAM_ID, team1.id.value)
                .set(SERIES_RESULTS.LOSER_TEAM_ID, team2.id.value)
                .execute()
            repo.complete(created.id, Instant.parse("2026-07-14T23:12:04Z"))
            val at = Instant.parse("2026-07-15T09:00:00Z")

            val reopened = repo.reopen(created.id, at)
            reopened.shouldNotBeNull()

            reopened.completed shouldBe false
            reopened.completedAt.shouldBeNull()
            reopened.reopenedAt shouldBe at
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

            repo.complete(first.id, Instant.parse("2026-07-14T23:12:04Z"))

            repo.getAllByEventId(orderingEvent.id).map { it.id } shouldBe expected
            repo.getAllByEventId(orderingEvent.id).map { it.id } shouldBe expected
        }

        test("completion filtering splits two series that are otherwise identical") {
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
            repo.complete(closed.id, Instant.parse("2026-07-14T23:12:04Z"))
            val all = repo.getAllByEventId(filterEvent.id)

            all
                .filterByCompletion(
                    SeriesQuery(teamIds = null, eventStage = null, completed = false),
                ).map { it.id } shouldBe listOf(open.id)

            all
                .filterByCompletion(
                    SeriesQuery(teamIds = null, eventStage = null, completed = true),
                ).map { it.id } shouldBe listOf(closed.id)

            all
                .filterByCompletion(
                    SeriesQuery(teamIds = null, eventStage = null),
                ).map { it.id } shouldBe listOf(open.id, closed.id)
        }

        test("the V011 backfill completes series in ended events and leaves active ones alone") {
            val e = EventRepository(dsl)
            val endedEvent =
                e.insert(
                    NewEvent(
                        name = "Ended".toEventName(),
                        description = "An event that has already finished.".toEventDescription(),
                        startDate = Instant.now().minusSeconds(7_200L),
                        endDate = Instant.now().minusSeconds(3_600L),
                        status = EventStatus.COMPLETED,
                        eventStages = setOf(EventStage.REGULAR_SEASON),
                    ),
                    riotTournamentId = 2.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert ended event.")
            val stale =
                repo.insert(newSeries.copy(eventId = endedEvent.id))
                    ?: throw Exception("Failed to insert series in ended event.")
            val active =
                repo.insert(newSeries)
                    ?: throw Exception("Failed to insert series in active event.")

            dsl.execute(
                """
                UPDATE dennys.series s SET completed = true, completed_at = e.end_date
                  FROM dennys.events e WHERE e.id = s.event_id AND e.end_date < now()
                """.trimIndent(),
            )

            repo.getById(stale.id)?.completed shouldBe true
            repo.getById(stale.id)?.completedAt shouldBe endedEvent.endDate
            repo.getById(active.id)?.completed shouldBe false
            repo.getById(active.id)?.completedAt.shouldBeNull()
        }
    })
