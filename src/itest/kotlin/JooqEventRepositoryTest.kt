import com.lowbudgetlcs.domain.models.tournament.TournamentId
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.toEventGroupId
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

class JooqEventRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dslContext = DSL.using(ds, SQLDialect.POSTGRES)

    test("Insert NewEvent and retrieve by id") {
        val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val newEvent = NewEvent("Season 1", "The first season", 0.toEventGroupId(), now, now, EventStatus.ACTIVE)
        val event = JooqEventRepository(dslContext).insert(newEvent, TournamentId(1))
        withClue("result should be present") {
            event shouldNotBe null
        }
        event!!
        val result = JooqEventRepository(dslContext).getById(event.id)

        result shouldBe event
    }
})