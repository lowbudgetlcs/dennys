import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.gateways.ITournamentGateway
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.time.Instant.now
import java.time.temporal.ChronoUnit


class EventServiceTest : FunSpec({
    val eventRepo = mockk<IEventRepository>()
    val tournamentGate = mockk<ITournamentGateway>()
    val eventGroupRepo = mockk<IEventGroupRepository>()
    val service = EventService(eventRepo, eventGroupRepo, tournamentGate)
    val start = now()
    val end = now().plusSeconds(3600L)
    val newEvent = NewEvent(
        name = "Test", description = "This is a test.", startDate = start, endDate = end, status = EventStatus.ACTIVE
    )
    val expectedEvent = newEvent.toEvent(
        id = 0.toEventId(),
        createdAt = now().truncatedTo(ChronoUnit.MILLIS),
        tournamentId = 9999.toTournamentId(),
    )
    val newTournament = NewTournament("Test")
    val newGroup1 = NewEventGroup("Group 1")

    test("Creating event succeeds") {
        every {
            tournamentGate.create(newTournament)
        } returns Tournament(
            id = 9999.toTournamentId(), name = "Test"
        )
        every { eventRepo.insert(newEvent, 9999.toTournamentId()) } returns expectedEvent
        every { eventRepo.getAll() } returns listOf()
        val event = service.createEvent(newEvent, NewTournament("Test"))
        event.shouldNotBeNull()
        // We ignore generated fields as those are out-of-scope for this test
        event.shouldBeEqualToIgnoringFields(
            expectedEvent, Event::id, Event::tournamentId, Event::createdAt
        )
    }

    test("getEvent() returns valid event") {
        every {
            tournamentGate.create(newTournament)
        } returns Tournament(
            id = 9999.toTournamentId(), name = "Test"
        )
        every { eventRepo.insert(newEvent, 9999.toTournamentId()) } returns expectedEvent
        every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
        every { eventRepo.getAll() } returns listOf()
        val e = service.createEvent(newEvent, newTournament)
        e.shouldNotBeNull()
        val fetched = service.getEvent(e.id)
        fetched shouldBe e
    }

    test("Fetching event that doesn't exist returns null") {
        every {
            tournamentGate.create(newTournament)
        } returns Tournament(
            id = 9999.toTournamentId(), name = "Test"
        )
        every { eventRepo.insert(newEvent, 9999.toTournamentId()) } returns expectedEvent
        every { eventRepo.getAll() } returns listOf()
        val e = service.createEvent(newEvent, newTournament)
        e.shouldNotBeNull()
        val fetched = service.getEvent((expectedEvent.id.value + 1).toEventId())
        fetched shouldBe null
    }

    test("Fetching negative returns null") {
        every {
            tournamentGate.create(newTournament)
        } returns Tournament(
            id = 9999.toTournamentId(), name = "Test"
        )
        every { eventRepo.insert(newEvent, 9999.toTournamentId()) } returns expectedEvent
        every { eventRepo.getAll() } returns listOf(expectedEvent)
        val e = service.createEvent(newEvent, newTournament)
        e.shouldNotBeNull()
        val fetched = service.getEvent((-1).toEventId())
        fetched shouldBe null
    }

    xtest("createEventGroup() returns valid group") {
        every { eventGroupRepo.insert(newGroup1) } returns newGroup1.toEventGroup(1.toEventGroupId())
        val group = service.createEventGroup(newGroup1)
        group.shouldNotBeNull()
        group.shouldBeEqualToIgnoringFields(newGroup1.toEventGroup(0.toEventGroupId()), EventGroup::id)
    }
})