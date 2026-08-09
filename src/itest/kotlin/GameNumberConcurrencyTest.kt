import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

private const val CONCURRENT_WRITERS = 8

class GameNumberConcurrencyTest :
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
                maximumPoolSize = CONCURRENT_WRITERS
            }
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = GameRepository(dsl)
        lateinit var event: Event
        lateinit var team1: Team
        lateinit var team2: Team
        lateinit var seriesRepo: SeriesRepository

        beforeSpec {
            event = EventRepository(dsl).insert(
                NewEvent(
                    name = "Concurrency".toEventName(),
                    description = "Concurrent game number derivation.".toEventDescription(),
                    startDate = Instant.now(),
                    endDate = Instant.now().plusSeconds(3_600L),
                    status = EventStatus.ACTIVE,
                    eventStages = setOf(EventStage.REGULAR_SEASON),
                ),
                riotTournamentId = 1.toRiotTournamentId(),
            ) ?: throw Exception("Failed to insert event.")
            val t = TeamRepository(dsl)
            team1 = t.insert(NewTeam(name = "Team 1".toTeamName())) ?: throw Exception("Failed to insert team 1.")
            team2 = t.insert(NewTeam(name = "Team 2".toTeamName())) ?: throw Exception("Failed to insert team 2.")
            seriesRepo = SeriesRepository(dsl)
        }

        fun newSeries(): Series =
            seriesRepo.insert(
                NewSeries(
                    eventId = event.id,
                    totalGames = 99,
                    participantIds = Pair(team1.id, team2.id),
                    eventStage = EventStage.REGULAR_SEASON,
                ),
            ) ?: throw Exception("Failed to insert series.")

        test("concurrent writers to one series get distinct consecutive game numbers") {
            val series = newSeries()

            coroutineScope {
                (1..CONCURRENT_WRITERS)
                    .map {
                        async(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                repo.insert(NewGame(series.id, null, null, null))
                            }
                        }
                    }.awaitAll()
            }

            val numbers = repo.getBySeriesId(series.id).map { it.number }
            numbers shouldBe (1..CONCURRENT_WRITERS).toList()
            numbers.toSet().size shouldBe CONCURRENT_WRITERS
        }

        test("concurrent writers to different series do not block or collide") {
            val a = newSeries()
            val b = newSeries()

            coroutineScope {
                listOf(a, b)
                    .flatMap { s -> (1..4).map { s } }
                    .map { s -> async(Dispatchers.IO) { repo.insert(NewGame(s.id, null, null, null)) } }
                    .awaitAll()
            }

            repo.getBySeriesId(a.id).map { it.number } shouldBe listOf(1, 2, 3, 4)
            repo.getBySeriesId(b.id).map { it.number } shouldBe listOf(1, 2, 3, 4)
        }
    })
