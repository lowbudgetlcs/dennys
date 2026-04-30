package services.event

import com.lowbudgetlcs.domain.event.EventService
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.RiotTournament
import com.lowbudgetlcs.domain.event.models.toEvent
import com.lowbudgetlcs.domain.event.models.types.toEventDescription
import com.lowbudgetlcs.domain.event.models.types.toEventId
import com.lowbudgetlcs.domain.event.models.types.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.enums.EventStage
import com.lowbudgetlcs.domain.event.models.enums.EventStatus
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.domain.event.repositories.IEventRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventServiceTest :
    FunSpec({
        val eventRepo = mockk<IEventRepository>()
        val tournamentGate = mockk<IRiotTournamentGateway>()
        val service = EventService(eventRepo, tournamentGate, mockk<ITeamRepository>(), mockk<ISeriesRepository>())
        val start = Instant.now()
        val end = Instant.now().plusSeconds(3600L)
        val newEvent =
            NewEvent(
                name = "Test".toEventName(),
                description = "This is a test.".toEventDescription(),
                startDate = start,
                endDate = end,
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val expectedEvent =
            newEvent.toEvent(
                id = 0.toEventId(),
                createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                riotTournamentId = 9999.toRiotTournamentId(),
            )
        val newRiotTournament = expectedEvent.name

        beforeTest {
            coEvery {
                tournamentGate.create(newRiotTournament)
            } returns
                RiotTournament(
                    id = 9999.toRiotTournamentId(),
                    name = "Test".toEventName(),
                )
        }

        test("Creating event succeeds") {
            every { eventRepo.insert(newEvent, 9999.toRiotTournamentId()) } returns expectedEvent
            every { eventRepo.getByName(newEvent.name) } returns null
            val event = service.createEvent(newEvent)
            event.shouldNotBeNull()
            event shouldBe expectedEvent
        }

        test("getEvent() returns valid event") {
            every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
            val fetched = service.getEvent(expectedEvent.id)
            fetched shouldBe expectedEvent
        }

        test("Fetching event that doesn't exist throws NoSuchElementException") {
            every { eventRepo.getById(expectedEvent.id) } returns null
            shouldThrow<NoSuchElementException> { service.getEvent(expectedEvent.id) }
        }
    })
