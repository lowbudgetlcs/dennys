package com.lowbudgetlcs.domain.services.event.group

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupUpdate
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup

interface IEventGroupService {
    fun getAllEventGroups(): List<EventGroup>

    fun getEventGroup(id: EventGroupId): EventGroup

    fun createEventGroup(group: NewEventGroup): EventGroup

    fun patchEventGroup(
        id: EventGroupId,
        update: EventGroupUpdate,
    ): EventGroup

    fun getEventGroupWithEvents(id: EventGroupId): List<EventGroupWithEvents>

    fun addEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents

    fun removeEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents
}
