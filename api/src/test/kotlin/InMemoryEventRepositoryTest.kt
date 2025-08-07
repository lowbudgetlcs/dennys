import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.repositories.inmemory.InMemoryEventRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.Instant.now

class InMemoryEventRepositoryTest : StringSpec({
    val repo = InMemoryEventRepository()
    val start = now()
    val end = now().plusSeconds(60000)
    val newEvent = NewEvent(
        name = "Test",
        description = "This is a test.",
        eventGroupId = null,
        startDate = start,
        endDate = end,
        status = EventStatus.ACTIVE
    )
    beforeTest {
        repo.clear()
    }
    "insert() accepts a NewEvent and returns a valid Event" {
        val event = repo.insert(newEvent, 0.toTournamentId())
        event.shouldBeInstanceOf<Event>()
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
            ), Event::createdAt
        )
    }
    "getById() fetches the correct event" {
        val event = repo.insert(newEvent, 1.toTournamentId())
        val fetched = repo.getById(0.toEventId())
        event shouldBe fetched
    }
    "getById() returns null when array is empty" {
        val e = repo.getById(0.toEventId())
        e shouldBe null
    }
    "getById() returns null on out-of-bounds access" {
        val event = repo.insert(newEvent, 1.toTournamentId())
        val bigger = repo.getById(10.toEventId())
        val smaller = repo.getById((-1).toEventId())
        bigger shouldBe null
        smaller shouldBe null
        repo.getById(0.toEventId()) shouldBe event
    }
    "getAll() gets all events" {
        val elements = 9
        for (i in 0..elements) {
            repo.insert(newEvent, i.toTournamentId())
        }
        val events = repo.getAll()
        events.shouldHaveSize(elements+1)
        for (i in events.indices) {
            events[i].shouldBeEqualToIgnoringFields(
                Event(
                    i.toEventId(),
                    "Test",
                    "This is a test.",
                    eventGroupId = null,
                    i.toTournamentId(),
                    now(),
                    start,
                    end,
                    EventStatus.ACTIVE
                ), Event::createdAt
            )
        }
    }
})