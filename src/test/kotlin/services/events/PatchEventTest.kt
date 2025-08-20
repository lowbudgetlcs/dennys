package services.events

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.EventUpdate
import com.lowbudgetlcs.domain.models.events.patch
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.gateways.ITournamentGateway
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class PatchEventTest : FunSpec({
    val eventRepo = mockk<IEventRepository>()
    val service = EventService(eventRepo, mockk<IEventGroupRepository>(), mockk<ITournamentGateway>())
    val start = Instant.now()
    val end = start.plusSeconds(40_000L)
    val expectedEvent = Event(
        id = 0.toEventId(),
        createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        tournamentId = 9999.toTournamentId(),
        name = "Test",
        description = "This is a test.",
        eventGroupId = null,
        startDate = start,
        endDate = end,
        status = EventStatus.NOT_STARTED,
    )

    beforeTest {
        every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
    }

    /* service.patchEvent() */
    test("patchEvent() throws exception when event id not found") {
        every { eventRepo.getById(expectedEvent.id) } returns null
        shouldThrow<NoSuchElementException> {
            service.patchEvent(expectedEvent.id, EventUpdate())
        }
    }

    test("patchEvent() does nothing when update is empty") {
        every { eventRepo.update(expectedEvent) } returns expectedEvent

        val event = service.patchEvent(expectedEvent.id, EventUpdate())
        event shouldBe expectedEvent
    }

    test("patchEvent() updates name field") {
        val name = "ABCDEFG"
        val update = EventUpdate(name = name)
        val patched = expectedEvent.patch(update)
        every { eventRepo.getAll() } returns listOf(expectedEvent)
        every { eventRepo.update(patched) } returns patched

        val event = service.patchEvent(expectedEvent.id, update)
        event.name shouldBe name
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::name)
        event.name shouldNotBe expectedEvent.name
    }

    test("patchEvent() updates description field") {
        val description = "ABCDEFG"
        val update = EventUpdate(description = description)
        val patched = expectedEvent.patch(update)
        every { eventRepo.update(patched) } returns patched

        val event = service.patchEvent(expectedEvent.id, EventUpdate(description = description))
        event.description shouldBe description
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::description)
        event.description shouldNotBe expectedEvent.description
    }

    test("patchEvent() updates startDate field") {
        val startDate = Instant.now()
        val update = EventUpdate(startDate = startDate)
        val patched = expectedEvent.patch(update)
        every { eventRepo.update(patched) } returns patched

        val event = service.patchEvent(expectedEvent.id, EventUpdate(startDate = startDate))
        event.startDate shouldBe startDate
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::startDate)
        event.startDate shouldNotBe expectedEvent.startDate
    }

    test("patchEvent() updates endDate field") {
        val endDate = Instant.now()
        val update = EventUpdate(endDate = endDate)
        val patched = expectedEvent.patch(update)
        every { eventRepo.update(patched) } returns patched

        val event = service.patchEvent(expectedEvent.id, EventUpdate(endDate = endDate))
        event.endDate shouldBe endDate
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::endDate)
        event.endDate shouldNotBe expectedEvent.endDate
    }

    test("patchEvent() updates status field") {
        val status = EventStatus.CANCELED
        val update = EventUpdate(status = status)
        val patched = expectedEvent.patch(update)
        every { eventRepo.update(patched) } returns patched

        val event = service.patchEvent(expectedEvent.id, EventUpdate(status = status))
        event.status shouldBe status
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::status)
        event.status shouldNotBe expectedEvent.status
    }

    test("patchEvent() cannot invalidate start and end dates.") {
        shouldThrow<IllegalArgumentException> {
            service.patchEvent(
                expectedEvent.id, EventUpdate(endDate = expectedEvent.startDate, startDate = expectedEvent.endDate)
            )
        }
    }
    test("patchEvent() throws exception when name is taken") {
        every { eventRepo.getAll() } returns listOf(expectedEvent)
        shouldThrow<IllegalArgumentException> {
            service.patchEvent(expectedEvent.id, EventUpdate(name = expectedEvent.name))
        }
    }
})
