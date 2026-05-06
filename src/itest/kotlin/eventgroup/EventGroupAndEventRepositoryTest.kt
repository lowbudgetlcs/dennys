package eventgroup

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.division.adapter.out.persistence.SqlEventGroupRepository
import com.lowbudgetlcs.domain.division.adapter.out.persistence.SqlEventRepository
import com.lowbudgetlcs.domain.division.core.model.Event
import com.lowbudgetlcs.domain.division.core.model.EventGroup
import com.lowbudgetlcs.domain.division.core.model.EventUpdate
import com.lowbudgetlcs.domain.division.core.model.NewEvent
import com.lowbudgetlcs.domain.division.core.model.NewEventGroup
import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.model.toRiotTournamentId
import com.lowbudgetlcs.domain.division.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.division.core.model.types.toEventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.toEventGroupName
import com.lowbudgetlcs.domain.division.core.model.types.toEventName
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventGroupAndEventRepositoryTest :
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
        val eventGroupRepo = SqlEventGroupRepository(dslContext)
        val eventRepo = SqlEventRepository(dslContext)
        // Data
        val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
        lateinit var group: EventGroup
        lateinit var event: Event
        val newEvent =
            NewEvent(
                "Test Event".toEventName(),
                description = "Hello World!".toEventDescription(),
                startDate = now,
                endDate = now.plus(1, ChronoUnit.HOURS),
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        beforeSpec {
            event = eventRepo.insert(
                newEvent,
                12345.toRiotTournamentId(),
            ) ?: throw Exception("Failed to insert initial event.")
            group = eventGroupRepo.insert(NewEventGroup("Test Group".toEventGroupName()))
                ?: throw Exception("Failed to insert initial event group.")
        }

        "Adding event to valid event group succeeds." {
            val updated = eventRepo.update(event, EventUpdate(eventGroupId = PatchField.Value(group.id)))
            updated.shouldNotBeNull()
            updated.shouldBeEqualToIgnoringFields(event, Event::eventGroupId)
            updated.eventGroupId shouldBe group.id
        }

        "Adding event to invalid event group fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                eventRepo.update(event, EventUpdate(eventGroupId = PatchField.Value((-1).toEventGroupId())))
            }
        }

        "Creating event with valid event group succeeds." {
            val event =
                eventRepo.insert(
                    newEvent.copy(name = "NEW NEW".toEventName(), eventGroupId = group.id),
                    54321.toRiotTournamentId(),
                )
            event.shouldNotBeNull()
            event.eventGroupId shouldBe group.id
        }

        "Creating event with invalid event group fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                eventRepo.insert(
                    newEvent.copy(name = "NEW NEW".toEventName(), eventGroupId = (-1).toEventGroupId()),
                    432.toRiotTournamentId(),
                )
            }
        }
    })
