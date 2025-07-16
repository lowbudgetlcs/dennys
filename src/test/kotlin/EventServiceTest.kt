import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.EventWithGroup
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventGroupRepository
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventRepository
import com.lowbudgetlcs.repositories.inmemory.InMemoryTournamentRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.Instant.now


class EventServiceTest : StringSpec({
    val eventRepo = InMemoryEventRepository()
    val tournamentRepo = InMemoryTournamentRepository()
    val eventGroupRepo = InMemoryEventGroupRepository()
    val service = EventService(eventRepo, eventGroupRepo, tournamentRepo)
    val start = now()
    val end = now().plusSeconds(3600L)
    val newEvent = NewEvent(
        name = "Test",
        description = "This is a test.",
        startDate = start,
        endDate = end,
        eventGroupId = null,
        status = EventStatus.ACTIVE
    )

    beforeTest {
        eventRepo.clear()
        tournamentRepo.clear()
    }

    "Creating event succeeds" {
        val event = service.createEvent(newEvent, NewTournament("Test"))
        event.shouldBeInstanceOf<Event>()
        // We ignore generated fields as those are out-of-scope for this test
        event.shouldBeEqualToIgnoringFields(
            Event(
                0.toEventId(),
                "Test",
                "This is a test.",
                eventGroupId = null,
                0.toTournamentId(),
                now(),
                start,
                end,
                EventStatus.ACTIVE
            ), Event::id, Event::tournamentId, Event::createdAt
        )
    }

    "Fetching out-of-bounds returns null" {
        service.createEvent(newEvent, NewTournament("Test"))
        val fetched = service.getEvent(10.toEventId())
        fetched shouldBe null
    }
    "Fetching negative returns null" {
        service.createEvent(newEvent, NewTournament("Test"))
        val fetched = service.getEvent((-1).toEventId())
        fetched shouldBe null
    }
    "getEvents() returns all events with their respective groups" {
        val group1 = service.createEventGroup(NewEventGroup("Group 1"))
        val group2 = service.createEventGroup(NewEventGroup("Group 2"))
        val elements = 15
        for (i in 0..elements) {
            if (i % 3 == 0) {
                service.createEvent(newEvent.copy(eventGroupId = group1.id), NewTournament("$i"))
            } else if (i % 4 == 0) {
                service.createEvent(newEvent.copy(eventGroupId = group2.id), NewTournament("$i"))
            } else {
                service.createEvent(newEvent, NewTournament("$i"))
            }
        }
        val events = service.getEvents()
        events.shouldBeInstanceOf<List<EventWithGroup>>()
        events.shouldHaveSize(elements+1)
        for (e in events) {
            if (e.id.value % 3 == 0) {
                e.eventGroup shouldBe group1
            } else if (e.id.value % 4 == 0) {
                e.eventGroup shouldBe group2
            } else {
                e.eventGroup shouldBe null
            }
        }
    }
})

