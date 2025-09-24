package com.lowbudgetlcs.domain.services.event.group

import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup

interface IEventGroupService {
    fun getAllEventGroups(): List<EventGroup>

    fun getEventGroupWithEvents(id: EventGroupId): List<EventGroupWithEvents>

    fun createEventGroup(group: NewEventGroup): EventGroup

    fun getEventGroupById(id: EventGroupId): EventGroup
}
