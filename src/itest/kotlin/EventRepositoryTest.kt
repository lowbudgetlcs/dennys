import com.lowbudgetlcs.domain.event.core.model.Event
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.event.core.model.types.toEventId
import com.lowbudgetlcs.domain.event.core.model.types.toEventName
import com.lowbudgetlcs.domain.event.core.model.toRiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.event.adapter.out.persistence.SqlEventRepository
import com.lowbudgetlcs.domain.event.core.model.RiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.types.EventId
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

fun NewEvent.toEvent(
    id: EventId,
    createdAt: Instant,
    riotTournamentId: RiotTournamentId,
): Event =
    Event(
        id = id,
        name = name,
        description = description,
        riotTournamentId = riotTournamentId,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        eventGroupId = null,
        status = status,
        eventStages = eventStages,
    )

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
        val repo = SqlEventRepository(dslContext)

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
