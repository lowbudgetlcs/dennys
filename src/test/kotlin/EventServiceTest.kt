import com.lowbudgetlcs.domain.models.event.Event
import com.lowbudgetlcs.domain.models.event.EventStatus
import com.lowbudgetlcs.domain.models.event.NewEvent
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.domain.tournament.NewTournament
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventRepository
import com.lowbudgetlcs.repositories.inmemory.InMemoryTournamentRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.Instant.now


class EventServiceTest : StringSpec({
    val eventRepo = InMemoryEventRepository()
    val tournamentRepo = InMemoryTournamentRepository()
    val service = EventService(eventRepo, tournamentRepo)

    "Creating event returns valid event" {
        val start = now()
        val end = now().plusSeconds(3600L)
        val e = NewEvent(
            name = "Test",
            description = "This is a test.",
            startDate = start,
            endDate = end,
            status = EventStatus.ACTIVE
        )
        val t = NewTournament()
        val event = service.create(e, t)
        event.shouldBeInstanceOf<Event>()
        event.id shouldBe 0
        event.name shouldBe "Test"
        event.description shouldBe "This is a test."
        event.startDate shouldBe start
        event.endDate shouldBe end
        event.status shouldBe EventStatus.ACTIVE
        event.riotTournamentId shouldBe 0
    }
})

