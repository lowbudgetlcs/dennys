import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroupId
import com.lowbudgetlcs.domain.models.events.group.toEventGroupName
import com.lowbudgetlcs.repositories.event.group.EventGroupRepository
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
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class EventGroupRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = EventGroupRepository(dslContext)

        // Data
        val newGroup = NewEventGroup(name = "Test Group".toEventGroupName())

        "getAll() starts empty" {
            val groups = repo.getAll()
            groups.shouldBeInstanceOf<List<EventGroup>>()
            groups.shouldBeEmpty()
        }

        "insert() creates new EventGroup, getById() fetches correct EventGroup" {
            val created = repo.insert(newGroup)
            created.shouldNotBeNull()
            created.shouldBeEqualToIgnoringFields(
                newGroup.toEventGroup(0.toEventGroupId()),
                EventGroup::id,
            )
            val group = repo.getById(created.id)
            group shouldBe created
        }

        "insert() cannot insert duplicate EventGroup" {
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newGroup)
            }
        }

        "getAll() returns all event groups" {
            val groups = repo.getAll()
            groups.shouldBeInstanceOf<List<EventGroup>>()
            groups.shouldHaveSize(1)
            groups[0].name shouldBe newGroup.name
        }
    })
