package com.lowbudgetlcs.domain.services.event.group

import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository

class EventGroupService(private val eventGroupRepo: IEventGroupRepository, private val eventRepo: IEventRepository) :
    IEventGroupService {
    override fun getAllEventGroups(): List<EventGroup> = eventGroupRepo.getAll()

    override fun getEventGroupWithEvents(id: EventGroupId): List<EventGroupWithEvents> = TODO("Not implemented.")

    override fun getEventGroupById(id: EventGroupId): EventGroup = TODO("Not implemented.")

    override fun createEventGroup(group: NewEventGroup): EventGroup = TODO("Not implemented.")
}