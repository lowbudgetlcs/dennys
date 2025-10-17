package services.events

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.EventUpdate
import com.lowbudgetlcs.domain.models.events.patch
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.services.event.EventService
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class PatchEventTest :
    FunSpec({
        val eventRepo = mockk<IEventRepository>()
        val service =
            EventService(
                eventRepo,
                mockk<IRiotTournamentGateway>(),
                mockk<ITeamRepository>(),
                mockk<ISeriesRepository>(),
            )
        val start = Instant.now()
        val end = start.plusSeconds(40_000L)
        val testEvent =
            Event(
                id = 0.toEventId(),
                createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                riotTournamentId = 9999.toRiotTournamentId(),
                name = "Test",
                description = "This is a test.",
                eventGroupId = null,
                startDate = start,
                endDate = end,
                status = EventStatus.NOT_STARTED,
            )

        beforeTest { every { eventRepo.getById(testEvent.id) } returns testEvent }

        // service.patchEvent()
        test("patchEvent() throws exception when event id not found") {
            every { eventRepo.getById(testEvent.id) } returns null
            shouldThrow<NoSuchElementException> {
                service.patchEvent(testEvent.id, EventUpdate())
            }
        }

        test("patchEvent() does nothing when update is empty") {
            every { eventRepo.update(testEvent) } returns testEvent

            val event = service.patchEvent(testEvent.id, EventUpdate())
            event shouldBe testEvent
        }

        test("patchEvent() updates name field") {
            val name = "ABCDEFG"
            val update = EventUpdate(name = name)
            val patched = testEvent.patch(update)
            every { eventRepo.getByName(name) } returns null
            every { eventRepo.update(patched) } returns patched

            val event = service.patchEvent(testEvent.id, update)
            event.name shouldBe name
            event.shouldBeEqualToIgnoringFields(testEvent, Event::name)
            event.name shouldNotBe testEvent.name
        }

        test("patchEvent() updates description field") {
            val description = "ABCDEFG"
            val update = EventUpdate(description = description)
            val patched = testEvent.patch(update)
            every { eventRepo.update(patched) } returns patched

            val event = service.patchEvent(testEvent.id, EventUpdate(description = description))
            event.description shouldBe description
            event.shouldBeEqualToIgnoringFields(testEvent, Event::description)
            event.description shouldNotBe testEvent.description
        }

        test("patchEvent() updates startDate field") {
            val startDate = Instant.now()
            val update = EventUpdate(startDate = startDate)
            val patched = testEvent.patch(update)
            every { eventRepo.update(patched) } returns patched

            val event = service.patchEvent(testEvent.id, EventUpdate(startDate = startDate))
            event.startDate shouldBe startDate
            event.shouldBeEqualToIgnoringFields(testEvent, Event::startDate)
            event.startDate shouldNotBe testEvent.startDate
        }

        test("patchEvent() updates endDate field") {
            val endDate = Instant.now()
            val update = EventUpdate(endDate = endDate)
            val patched = testEvent.patch(update)
            every { eventRepo.update(patched) } returns patched

            val event = service.patchEvent(testEvent.id, EventUpdate(endDate = endDate))
            event.endDate shouldBe endDate
            event.shouldBeEqualToIgnoringFields(testEvent, Event::endDate)
            event.endDate shouldNotBe testEvent.endDate
        }

        test("patchEvent() updates status field") {
            val status = EventStatus.CANCELED
            val update = EventUpdate(status = status)
            val patched = testEvent.patch(update)
            every { eventRepo.update(patched) } returns patched

            val event = service.patchEvent(testEvent.id, EventUpdate(status = status))
            event.status shouldBe status
            event.shouldBeEqualToIgnoringFields(testEvent, Event::status)
            event.status shouldNotBe testEvent.status
        }

        test("patchEvent() cannot invalidate start and end dates.") {
            shouldThrow<IllegalArgumentException> {
                service.patchEvent(
                    testEvent.id,
                    EventUpdate(
                        endDate = testEvent.startDate,
                        startDate = testEvent.endDate,
                    ),
                )
            }
        }
        test("patchEvent() throws exception when name is taken") {
            every { eventRepo.getByName(testEvent.name) } returns testEvent
            shouldThrow<IllegalArgumentException> {
                service.patchEvent(testEvent.id, EventUpdate(name = testEvent.name))
            }
        }
    })
