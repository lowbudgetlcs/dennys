package com.lowbudgetlcs.domain.services.event.group

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupUpdate
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.event.group.IEventGroupRepository

class EventGroupService(
    private val eventGroupRepo: IEventGroupRepository,
    private val eventRepo: IEventRepository,
) : IEventGroupService {
    override fun getAllEventGroups(): List<EventGroup> = eventGroupRepo.getAll()

    override fun getEventGroupWithEvents(id: EventGroupId): List<EventGroupWithEvents> = TODO("Not implemented.")

    override fun addEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents {
        TODO("Not yet implemented")
    }

    override fun removeEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents {
        TODO("Not yet implemented")
    }

    override fun getEventGroup(id: EventGroupId): EventGroup = TODO("Not implemented.")

    override fun createEventGroup(group: NewEventGroup): EventGroup = TODO("Not implemented.")

    override fun patchEventGroup(
        id: EventGroupId,
        update: EventGroupUpdate,
    ): EventGroup {
        TODO("Not yet implemented")
    }
}
