package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.domain.models.events.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.NewEventGroup

interface IEventGroupService {
    fun getAllEventGroups(): List<EventGroup>
    fun getEventGroupWithEvents(id: EventGroupId): List<EventGroupWithEvents>
    fun createEventGroup(group: NewEventGroup): EventGroup
    fun getEventGroupById(id: EventGroupId): EventGroup
}