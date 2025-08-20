import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.repositories.EventRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit


class EventRepositoryTest : StringSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
    val repo = EventRepository(dslContext)

    // Data
    var insertedEventCount = 100
    val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
    val newEvent = NewEvent(
        name = "Season 1",
        description = "The first season",
        startDate = now,
        endDate = now.plusSeconds(604_800L),
        status = EventStatus.ACTIVE
    )
    val newEvent2 = NewEvent(
        name = "Season 2",
        description = "The second season",
        startDate = now,
        endDate = now.plusSeconds(604_800L),
        status = EventStatus.ACTIVE
    )

    fun Arb.Companion.newEvent(count: Int): Arb<List<NewEvent>> = Arb.set(
        Arb.bind(
            Arb.string(minSize = 5, maxSize = 20), Arb.string(minSize = 0, maxSize = 100), Arb.instant(
                Instant.parse("2000-01-01T00:00:00Z"), Instant.parse("2030-01-01T00:00:00Z")
            ), Arb.enum<EventStatus>()
        ) { name, desc, start, status ->
            NewEvent(
                name = name,
                description = desc,
                startDate = start.truncatedTo(ChronoUnit.MICROS),
                endDate = start.truncatedTo(ChronoUnit.MICROS).plus(Arb.long(1, 604_800).next(), ChronoUnit.SECONDS),
                status = status
            )
        }, size = count
    ).map { it.toList() }

    fun Arb.Companion.newEventWithExpectedEvent(count: Int): Arb<List<Pair<NewEvent, Event>>> =
        newEvent(count).map { list ->
            list.map { new ->
                new to new.toEvent(
                    id = 1.toEventId(),
                    tournamentId = 1.toTournamentId(),
                    createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS)
                )
            }
        }

    "getAll() starts empty" {
        val events = repo.getAll()
        events.shouldBeInstanceOf<List<Event>>()
        events.shouldBeEmpty()
    }

    "insert() creates new Event" {
        checkAll(Arb.newEventWithExpectedEvent(insertedEventCount)) { newEvents ->
            newEvents.forEach { (new, expected) ->
                val created = repo.insert(new, 1.toTournamentId())
                created.shouldNotBeNull()
                created.shouldBeEqualToIgnoringFields(expected, Event::id, Event::createdAt, Event::tournamentId)
            }
        }
    }

    "insert() cannot insert duplicate Event" {
        repo.insert(newEvent, 1.toTournamentId())
        insertedEventCount++
        repo.insert(newEvent, 1.toTournamentId()) shouldBe null
    }

    "getById() fetches correct Event" {
        val created = repo.insert(newEvent2, 8888.toTournamentId())
        insertedEventCount++
        created.shouldNotBeNull()
        val event = repo.getById(created.id)
        event shouldBe created
    }

    "getAll() returns all events" {
        val events = repo.getAll()
        events.shouldBeInstanceOf<List<Event>>()
        events.shouldHaveSize(insertedEventCount)
    }
})