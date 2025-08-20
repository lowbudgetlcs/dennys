package services.events

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.EventUpdate
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
    val service = EventService(mockk<IEventRepository>(), mockk<IEventGroupRepository>(), mockk<ITournamentGateway>())
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
        every { service.getEvent(expectedEvent.id) } returns expectedEvent
    }

    /* service.patchEvent() */
    test("patchEvent() throws exception when event id not found") {
        every { service.getEvent(expectedEvent.id) } throws NoSuchElementException("Event not found.")
        shouldThrow<NoSuchElementException> {
            service.patchEvent(expectedEvent.id, EventUpdate())
        }
    }
    test("patchEvent() does nothing when update is empty") {
        val event = service.patchEvent(expectedEvent.id, EventUpdate())
        event shouldBe expectedEvent
    }

    test("patchEvent() updates name field") {
        val name = "ABCDEFG"
        val event = service.patchEvent(expectedEvent.id, EventUpdate(name = name))
        event.name shouldBe name
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::name)
        event.name shouldNotBe expectedEvent.name
    }

    test("patchEvent() updates description field") {
        val description = "ABCDEFG"
        val event = service.patchEvent(expectedEvent.id, EventUpdate(description = description))
        event.description shouldBe description
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::description)
        event.description shouldNotBe expectedEvent.description
    }

    test("patchEvent() updates startDate field") {
        val startDate = Instant.now()
        val event = service.patchEvent(expectedEvent.id, EventUpdate(startDate = startDate))
        event.startDate shouldBe startDate
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::startDate)
        event.startDate shouldNotBe expectedEvent.startDate
    }

    test("patchEvent() updates endDate field") {
        val endDate = Instant.now()
        val event = service.patchEvent(expectedEvent.id, EventUpdate(endDate = endDate))
        event.endDate shouldBe endDate
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::endDate)
        event.endDate shouldNotBe expectedEvent.endDate
    }

    test("patchEvent() updates status field") {
        val status = EventStatus.CANCELED
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
})
