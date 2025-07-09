import com.lowbudgetlcs.domain.models.event.EventStatus
import com.lowbudgetlcs.domain.models.event.NewEvent
import com.lowbudgetlcs.repositories.jooq.JooqEventRepository
import io.kotest.assertions.withClue
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dslContext = DSL.using(ds, SQLDialect.POSTGRES)

    test("Insert NewEvent and retrieve by id") {
        val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val event = NewEvent("Season 1", "The first season", now, now, EventStatus.ACTIVE)
        val result = JooqEventRepository(dslContext).insert(event, 1)
        withClue("result should be present") {
            result shouldNotBe null
        }
        result!!
        val entity = JooqEventRepository(dslContext).findById(result.id)

        entity shouldBe result
    }
})