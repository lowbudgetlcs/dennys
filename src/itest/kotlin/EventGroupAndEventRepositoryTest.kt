import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroupId
import com.lowbudgetlcs.domain.models.events.group.toEventGroupName
import com.lowbudgetlcs.domain.models.events.toEvent
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.event.group.EventGroupRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventGroupAndEventRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
        val eventGroupRepo = EventGroupRepository(dslContext)
        val eventRepo = EventRepository(dslContext)
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
        val expectedEvent =
            newEvent.toEvent(
                id = 1.toEventId(),
                createdAt = Instant.now(),
                riotTournamentId = 1245.toRiotTournamentId(),
            )
        val newGroup =
            NewEventGroup(
                name = "Test Group".toEventGroupName(),
            )
        val expectedGroup =
            newGroup.toEventGroup(
                0.toEventGroupId(),
            )

        fun checkEvent(event: Event) {
            event.shouldBeEqualToIgnoringFields(
                expectedEvent,
                Event::id,
                Event::createdAt,
                Event::riotTournamentId,
                Event::eventGroupId,
            )
        }

        fun checkEventGroup(eventGroup: EventGroup) {
            eventGroup.shouldBeEqualToIgnoringFields(expectedGroup, EventGroup::id)
        }

        "insert a new event group" {
            val created = eventGroupRepo.insert(newGroup)
            created.shouldNotBeNull()
            checkEventGroup(created)
        }

        "insert a new event" {
            val created = eventRepo.insert(newEvent, 1234.toRiotTournamentId())
            created.shouldNotBeNull()
            checkEvent(created)
            created.eventGroupId shouldBe null
        }

        "add event to event group" {
            val groups = eventGroupRepo.getAll()
            groups.shouldNotBeEmpty()
            val group = groups.first()
            val events = eventRepo.getAll()
            events.shouldNotBeEmpty()
            val event = events.first()
            val updated = eventRepo.update(event.copy(eventGroupId = group.id))
            updated.shouldNotBeNull()
            checkEvent(updated)
            updated.eventGroupId shouldBe group.id
        }
    })
