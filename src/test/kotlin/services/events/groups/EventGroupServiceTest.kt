package services.events.groups

import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroupId
import com.lowbudgetlcs.domain.models.events.group.toEventGroupName
import com.lowbudgetlcs.domain.services.event.group.EventGroupService
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.event.group.IEventGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

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
    })
