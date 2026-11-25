import com.lowbudgetlcs.domain.LiveEvent
import com.lowbudgetlcs.domain.LiveEvents
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.TournamentCodeRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class SeriesLiveEventsTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerSpecExtension(postgres)) { maximumPoolSize = 1 }
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)

        test("reported results emit series.updated with IN_PROGRESS, then COMPLETE on the winning game") {
            val event =
                EventRepository(dsl).insert(
                    NewEvent(
                        name = "Live".toEventName(),
                        description = "Testing live events.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.REGULAR_SEASON),
                    ),
                    riotTournamentId = 1.toRiotTournamentId(),
                )!!
            val teams = TeamRepository(dsl)
            val blue = teams.insert(NewTeam("Blue".toTeamName()))!!
            val red = teams.insert(NewTeam("Red".toTeamName()))!!
            val seriesRepo = SeriesRepository(dsl)
            val series = seriesRepo.insert(NewSeries(event.id, EventStage.REGULAR_SEASON, 3, blue.id to red.id))!!
            val live = LiveEvents()
            val service =
                SeriesService(
                    TournamentCodeRepository(dsl),
                    seriesRepo,
                    EventRepository(dsl),
                    teams,
                    GameRepository(dsl),
                    mockk(),
                    liveEvents = live,
                )

            fun updated(status: String) =
                LiveEvent(
                    "series.updated",
                    buildJsonObject {
                        put("seriesId", series.id.value)
                        put("eventId", event.id.value)
                        put("status", status)
                    },
                )

            coroutineScope {
                val events = async(start = CoroutineStart.UNDISPATCHED) { live.events.take(2).toList() }
                service.reportResult(series.id, ReportedResult(blue.id, red.id, null, null))
                service.reportResult(series.id, ReportedResult(blue.id, red.id, null, null))
                events.await() shouldBe listOf(updated("IN_PROGRESS"), updated("COMPLETE"))
            }
        }
    })
