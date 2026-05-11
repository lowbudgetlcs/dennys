import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.repositories.event.EventRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds =
            install(JdbcDatabaseContainerSpecExtension(postgres)) {
                maximumPoolSize = 1
            }
        val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = EventRepository(dslContext)

        // Data
        val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
        val newEvent =
            NewEvent(
                name = "Season 1".toEventName(),
                description = "The first season".toEventDescription(),
                startDate = now,
                endDate = now.plusSeconds(604_800L),
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val newEvent2 =
            NewEvent(
                name = "Season 2".toEventName(),
                description = "The second season".toEventDescription(),
                startDate = now,
                endDate = now.plusSeconds(604_800L),
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )

        "getAll() starts empty" {
            val events = repo.getAll()
            events.shouldBeInstanceOf<List<Event>>()
            events.shouldBeEmpty()
        }

        "insert() creates new Event" {
            val tid = 0.toRiotTournamentId()
            val event = repo.insert(newEvent, tid)
            event.shouldNotBeNull()
            event.shouldBeEqualToIgnoringFields(
                newEvent.toEvent(0.toEventId(), Instant.now(), tid),
                Event::id,
                Event::createdAt,
            )
        }

        "getById() fetches correct Event" {
            val created = repo.insert(newEvent2, 8888.toRiotTournamentId())
            created.shouldNotBeNull()
            val event = repo.getById(created.id)
            event shouldBe created
        }

        "getAll() returns all events" {
            val events = repo.getAll()
            events.shouldBeInstanceOf<List<Event>>()
            events.shouldHaveSize(2)
        }
    })
