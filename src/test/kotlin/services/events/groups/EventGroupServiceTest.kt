/*
package services.events.groups

import com.lowbudgetlcs.domain.Zeroable
import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.EventUpdate
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.EventGroupService
import com.lowbudgetlcs.domain.eventgroup.models.NewEventGroup
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroup
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupName
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.eventgroup.IEventGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventGroupServiceTest :
    FunSpec({
        val groupRepo = mockk<IEventGroupRepository>()
        val eventRepo = mockk<IEventRepository>()
        val service = EventGroupService(groupRepo, eventRepo)
        val newGroup =
            NewEventGroup(
                name = "Test".toEventGroupName(),
            )
        val expectedGroup =
            newGroup.toEventGroup(
                0.toEventGroupId(),
            )
        test("Blank event group name not allowed.") {
            shouldThrowAny { "".toEventGroupName() }
        }
        test("Valid event group name allowed.") {
            val name = "Season 14 Regular Season".toEventGroupName()
            name.shouldNotBeNull()
        }
        test("Creating event group succeeds.") {
            every { groupRepo.getByName(newGroup.name) } returns null
            every { groupRepo.insert(newGroup) } returns expectedGroup
            val group = service.createEventGroup(newGroup)
            group.shouldNotBeNull()
            group shouldBe expectedGroup
        }
        test("getEventGroup() returns valid event group") {
            every { groupRepo.getById(expectedGroup.id) } returns expectedGroup
            val group = service.getEventGroup(expectedGroup.id)
            group.shouldNotBeNull()
            group shouldBe expectedGroup
        }
        test("Fetching event that doesn't exist throws NoSuchElementException") {
            every { groupRepo.getById(any()) } returns null
            shouldThrow<NoSuchElementException> {
                service.getEventGroup(1.toEventGroupId())
            }
        }

        val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val expectedEvent1 =
            Event(
                id = 0.toEventId(),
                name = "Testing 1",
                description = "Testing stuff",
                eventGroupId = null,
                riotTournamentId = 0.toRiotTournamentId(),
                createdAt = now,
                startDate = now,
                endDate = now.plusSeconds(1L),
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val expectedEvent2 =
            Event(
                id = 1.toEventId(),
                name = "Testing 2",
                description = "Testing stuff",
                eventGroupId = null,
                riotTournamentId = 1.toRiotTournamentId(),
                createdAt = now,
                startDate = now,
                endDate = now.plusSeconds(1L),
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val addEvents = listOf(expectedEvent1.id, expectedEvent2.id)
        val newGroup2 =
            NewEventGroup(
                name = "Testing Group".toEventGroupName(),
                events = addEvents,
            )
        val expectedGroup2 = newGroup2.toEventGroup(1.toEventGroupId())

        test("Created event group with events succeeds.") {
            every { groupRepo.getByName(newGroup2.name) } returns null
            every { groupRepo.insert(newGroup2) } returns expectedGroup2
            every { groupRepo.getById(expectedGroup2.id) } returns expectedGroup2
            every { eventRepo.getById(expectedEvent1.id) } returns expectedEvent1
            every { eventRepo.getById(expectedEvent2.id) } returns expectedEvent2
            every { eventRepo.update(expectedEvent1, EventUpdate(eventGroupId = Zeroable(expectedGroup2.id))) } returns
                expectedEvent1.copy(
                    eventGroupId = expectedGroup2.id,
                )
            every { eventRepo.update(expectedEvent2, EventUpdate(eventGroupId = Zeroable(expectedGroup2.id))) } returns
                expectedEvent2.copy(
                    eventGroupId = expectedGroup2.id,
                )
            val group = service.createEventGroup(newGroup)
            group.shouldNotBeNull()
            group shouldBe expectedGroup
        }
    })


 */
