package services.events

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.EventService
import com.lowbudgetlcs.domain.event.models.*
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupId
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
                name = "Test".toEventName(),
                description = "This is a test.",
                eventGroupId = 10.toEventGroupId(),
                startDate = start,
                endDate = end,
                status = EventStatus.NOT_STARTED,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )

        beforeEach {
        }

        test("Event.patch() updates name") {
            val n = "New".toEventName()
            val update = EventUpdate(name = n)
            val new = testEvent.patch(update)
            new shouldNotBe testEvent
            new.name shouldBe n
        }
        test("Event.patch() updates description") {
            val d = "New description!!"
            val update = EventUpdate(description = d)
            val new = testEvent.patch(update)
            new shouldNotBe testEvent
            new.description shouldBe d
        }
        test("Event.patch() updates startDate and endDate") {
            val s1 = Instant.now()
            val s2 = Instant.now().plusSeconds(1)
            val update = EventUpdate(startDate = s1, endDate = s2)
            val new = testEvent.patch(update)
            new shouldNotBe testEvent
            new.startDate shouldBe s1
            new.endDate shouldBe s2
        }
        test("Event.patch() updates status") {
            val s = EventStatus.PAUSED
            val update = EventUpdate(status = s)
            val new = testEvent.patch(update)
            new shouldNotBe testEvent
            new.status shouldBe s
        }
        test("Event.patch() updates eventGroupId") {
            val e = 4.toEventGroupId()
            val update = EventUpdate(eventGroupId = PatchField.Value(e))
            val new = testEvent.patch(update)
            new shouldNotBe testEvent
            new.eventGroupId shouldBe e
        }
        test("Event.patch() NULLIFIES eventGroupId") {
            val update = EventUpdate(eventGroupId = PatchField.Value(null))
            val new = testEvent.patch(update)
            new shouldNotBe testEvent
            new.eventGroupId shouldBe null
        }

        // service.patchEvent()
        test("patchEvent() throws exception when event id not found") {
            every { eventRepo.getById(testEvent.id) } returns null
            every { eventRepo.update(testEvent, EventUpdate()) } returns testEvent

            shouldThrow<NoSuchElementException> {
                service.patchEvent(testEvent.id, EventUpdate())
            }
        }

        test("patchEvent() does nothing when update is empty") {
            val update = EventUpdate()
            every { eventRepo.getByName(any()) } returns null
            every { eventRepo.getById(testEvent.id) } returns testEvent
            every { eventRepo.update(testEvent, update) } returns testEvent

            val event = service.patchEvent(testEvent.id, update)
            event shouldBe testEvent
        }

        test("patchEvent() updates name field") {
            val name = "ABCDEFG".toEventName()
            val update = EventUpdate(name = name)
            val patched = testEvent.patch(update)
            every { eventRepo.getByName(name) } returns null
            every { eventRepo.getById(testEvent.id) } returns testEvent
            every { eventRepo.update(testEvent, update) } returns patched

            val event = service.patchEvent(testEvent.id, update)
            event.name shouldBe name
            event.shouldBeEqualToIgnoringFields(testEvent, Event::name)
            event.name shouldNotBe testEvent.name
        }

        test("patchEvent() updates description field") {
            val description = "ABCDEFG"
            val update = EventUpdate(description = description)
            val patched = testEvent.patch(update)
            every { eventRepo.getByName(any()) } returns null
            every { eventRepo.getById(testEvent.id) } returns testEvent
            every { eventRepo.update(testEvent, update) } returns patched

            val event = service.patchEvent(testEvent.id, update)
            event.description shouldBe description
            event.shouldBeEqualToIgnoringFields(testEvent, Event::description)
            event.description shouldNotBe testEvent.description
        }

        test("patchEvent() updates startDate field") {
            val startDate = Instant.now()
            val update = EventUpdate(startDate = startDate)
            val patched = testEvent.patch(update)
            every { eventRepo.getByName(any()) } returns null
            every { eventRepo.getById(testEvent.id) } returns testEvent
            every { eventRepo.update(testEvent, update) } returns patched

            val event = service.patchEvent(testEvent.id, update)
            event.startDate shouldBe startDate
            event.shouldBeEqualToIgnoringFields(testEvent, Event::startDate)
            event.startDate shouldNotBe testEvent.startDate
        }

        test("patchEvent() updates endDate field") {
            val endDate = Instant.now()
            val update = EventUpdate(endDate = endDate)
            val patched = testEvent.patch(update)
            every { eventRepo.getByName(any()) } returns null
            every { eventRepo.getById(testEvent.id) } returns testEvent
            every { eventRepo.update(testEvent, update) } returns patched

            val event = service.patchEvent(testEvent.id, update)
            event.endDate shouldBe endDate
            event.shouldBeEqualToIgnoringFields(testEvent, Event::endDate)
            event.endDate shouldNotBe testEvent.endDate
        }

        test("patchEvent() updates status field") {
            val status = EventStatus.CANCELED
            val update = EventUpdate(status = status)
            val patched = testEvent.patch(update)
            every { eventRepo.getByName(any()) } returns null
            every { eventRepo.getById(testEvent.id) } returns testEvent
            every { eventRepo.update(testEvent, update) } returns patched

            val event = service.patchEvent(testEvent.id, update)
            event.status shouldBe status
            event.shouldBeEqualToIgnoringFields(testEvent, Event::status)
            event.status shouldNotBe testEvent.status
        }

        test("patchEvent() cannot invalidate start and end dates.") {
            shouldThrow<IllegalStateException> {
                service.patchEvent(
                    testEvent.id,
                    EventUpdate(
                        startDate = testEvent.endDate,
                        endDate = testEvent.startDate,
                    ),
                )
            }
        }

        test("patchEvent() throws exception when name is taken") {
            every { eventRepo.getByName(testEvent.name) } returns testEvent
            shouldThrow<IllegalStateException> {
                service.patchEvent(testEvent.id, EventUpdate(name = testEvent.name))
            }
        }
    })
