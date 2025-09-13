import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.repositories.event.EventRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = EventRepository(dslContext)

        // Data
        val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
        val newEvent =
            NewEvent(
                name = "Season 1",
                description = "The first season",
                startDate = now,
                endDate = now.plusSeconds(604_800L),
                status = EventStatus.ACTIVE,
            )
        val newEvent2 =
            NewEvent(
                name = "Season 2",
                description = "The second season",
                startDate = now,
                endDate = now.plusSeconds(604_800L),
                status = EventStatus.ACTIVE,
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

        "insert() cannot insert duplicate Event" {
            repo.insert(newEvent, 1.toRiotTournamentId()) shouldBe null
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
