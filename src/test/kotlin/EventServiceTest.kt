import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventRepository
import com.lowbudgetlcs.repositories.inmemory.InMemoryTournamentRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.Instant.now


class EventServiceTest : StringSpec({
    val eventRepo = InMemoryEventRepository()
    val tournamentRepo = InMemoryTournamentRepository()
    val service = EventService(eventRepo, tournamentRepo)
    val start = now()
    val end = now().plusSeconds(3600L)

    beforeTest {
        eventRepo.clear()
        tournamentRepo.clear()
    }

    "Creating event succeeds" {
        val newEvent = NewEvent(
            name = "Test",
            description = "This is a test.",
            startDate = start,
            endDate = end,
            eventGroupId = null,
            status = EventStatus.ACTIVE
        )
        val event = service.create(newEvent, NewTournament("Test"))
        event.shouldBeInstanceOf<Event>()
        event.shouldBeEqualToIgnoringFields(
            Event(
                EventId(0),
                "Test",
                "This is a test.",
                eventGroupId = null,
                TournamentId(0),
                now(),
                start,
                end,
                EventStatus.ACTIVE
            ), Event::createdAt
        )
    }

    "Changing status from ACTIVE to CANCELED" {
        val newEvent = NewEvent(
            name = "Test",
            description = "This is a test.",
            startDate = start,
            endDate = end,
            eventGroupId = null,
            status = EventStatus.ACTIVE
        )
        val event = service.create(newEvent, NewTournament("Test"))
        event.shouldBeInstanceOf<Event>()
        val updatedEvent = service.changeEventStatus(
            event.id, EventStatus.CANCELED
        )
        updatedEvent.shouldBeInstanceOf<Event>()
        updatedEvent.status shouldBe EventStatus.CANCELED
    }
})

