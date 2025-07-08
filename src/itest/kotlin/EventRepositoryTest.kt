import com.lowbudgetlcs.domain.models.EventStatus
import com.lowbudgetlcs.dto.events.CreateEventDto
import com.lowbudgetlcs.repositories.EventRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.OffsetDateTime

class EventRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("migrations/"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dslContext = DSL.using(ds, SQLDialect.POSTGRES)

    test("create EvenDto, insert it, and retrieve it") {
        val now = OffsetDateTime.now()
        val event = CreateEventDto("Season 1", "The first season", 1, now, now, EventStatus.ACTIVE.name)
        EventRepository(dslContext).create(event)
        val entity = EventRepository(dslContext).getById(1)
        event shouldBe entity
    }
})