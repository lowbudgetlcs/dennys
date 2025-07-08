import com.lowbudgetlcs.dto.EventDto
import com.lowbudgetlcs.models.EventStatus
import com.lowbudgetlcs.repositories.EventRepository
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.time.OffsetDateTime

@Testcontainers
class EventRepositoryTest {
    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:15-alpine").apply {
            withCopyFileToContainer(MountableFile.forClasspathResource("migrations/"), "/docker-entrypoint-initdb.d/")
        }
    }

    @Test
    fun `test inserting and retrieving EventDto`() {
        val now = OffsetDateTime.now()
        val event = EventDto(1, "Season 1", "The first season", 1, now, now, now, EventStatus.ACTIVE)
        EventRepository().create(event)
        val entity = EventRepository.getById(1)
        assert(event.equals(entity))
    }
}