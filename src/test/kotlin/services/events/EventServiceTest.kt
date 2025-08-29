package services.events

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.services.event.EventService
import com.lowbudgetlcs.gateways.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.IEventRepository
import com.lowbudgetlcs.repositories.ISeriesRepository
import com.lowbudgetlcs.repositories.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventServiceTest : FunSpec({
    val eventRepo = mockk<IEventRepository>()
    val tournamentGate = mockk<IRiotTournamentGateway>()
    val service = EventService(eventRepo, tournamentGate, mockk<ITeamRepository>(), mockk<ISeriesRepository>())
    val start = Instant.now()
    val end = Instant.now().plusSeconds(3600L)
    val newEvent = NewEvent(
        name = "Test", description = "This is a test.", startDate = start, endDate = end, status = EventStatus.ACTIVE
    )
    val expectedEvent = newEvent.toEvent(
        id = 0.toEventId(),
        createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        riotTournamentId = 9999.toRiotTournamentId(),
    )
    val newRiotTournament = expectedEvent.name

    test("Creating event succeeds") {
        coEvery {
            tournamentGate.create(newRiotTournament)
        } returns RiotTournament(
            id = 9999.toRiotTournamentId(), name = "Test"
        )
        every { eventRepo.insert(newEvent, 9999.toRiotTournamentId()) } returns expectedEvent
        every { eventRepo.getAll() } returns listOf()
        val event = service.createEvent(newEvent)
        event.shouldNotBeNull()
        // We ignore generated fields as those are out-of-scope for this test
        event.shouldBeEqualToIgnoringFields(
            expectedEvent, Event::id, Event::riotTournamentId, Event::createdAt
        )
    }

    test("getEvent() returns valid event") {
        coEvery {
            tournamentGate.create(newRiotTournament)
        } returns RiotTournament(
            id = 9999.toRiotTournamentId(), name = "Test"
        )
        every { eventRepo.insert(newEvent, 9999.toRiotTournamentId()) } returns expectedEvent
        every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
        every { eventRepo.getAll() } returns listOf()
        val e = service.createEvent(newEvent)
        e.shouldNotBeNull()
        val fetched = service.getEvent(e.id)
        fetched shouldBe e
    }

    test("Fetching event that doesn't exist throws NoSuchElementException") {
        every { eventRepo.getById(expectedEvent.id) } returns null
        shouldThrow<NoSuchElementException> { service.getEvent(expectedEvent.id) }
    }
})
