package com.lowbudgetlcs.repositories.event.group

import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup

interface IEventGroupRepository {
    fun getAll(): List<EventGroup>

    fun getById(id: EventGroupId): EventGroup?

    fun insert(group: NewEventGroup): EventGroup?
}
