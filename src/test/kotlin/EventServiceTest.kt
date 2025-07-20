import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.gateways.MockTournamentGateway
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventGroupRepository
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.Instant.now


class EventServiceTest : StringSpec({
    val eventRepo = InMemoryEventRepository()
    val tournamentGate = MockTournamentGateway()
    val eventGroupRepo = InMemoryEventGroupRepository()
    val service = EventService(eventRepo, eventGroupRepo, tournamentGate)
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
    val newGroup1 = NewEventGroup("Group 1")
    NewEventGroup("Group 2")

    beforeTest {
        eventRepo.clear()
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
    "getEvent() returns valid event" {
        val e = service.createEvent(newEvent, NewTournament("Test"))
        e.shouldBeInstanceOf<Event>()
        val fetched = service.getEvent(e.id)
        fetched shouldBe e
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
    "createEventGroup() returns valid group" {
        val group = service.createEventGroup(newGroup1)
        group.shouldBeInstanceOf<EventGroup>()
        group.shouldBeEqualToIgnoringFields(newGroup1.toEventGroup(0.toEventGroupId()), EventGroup::id)
    }
    "getEventsWithGroups() returns only events with valid group references" {
        val group1 = service.createEventGroup(NewEventGroup("Group 1"))
        val group2 = service.createEventGroup(NewEventGroup("Group 2"))
        group1.shouldBeInstanceOf<EventGroup>()
        group2.shouldBeInstanceOf<EventGroup>()

        val elements = 15
        for (i in 0..elements) {
            val event = when {
                i % 3 == 0 -> newEvent.copy(eventGroupId = group1.id)
                i % 4 == 0 -> newEvent.copy(eventGroupId = group2.id)
                else -> newEvent.copy(eventGroupId = null)
            }
            service.createEvent(event, NewTournament("$i"))
        }

        val events = service.getEventsWithGroups()
        val expectedGrouped = (0..elements).count { it % 3 == 0 || it % 4 == 0 }

        events.shouldBeInstanceOf<List<EventWithGroup>>()
        events.shouldHaveSize(expectedGrouped)
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

