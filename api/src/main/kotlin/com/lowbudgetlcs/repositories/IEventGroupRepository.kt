package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.domain.models.events.NewEventGroup

interface IEventGroupRepository {
    fun getAll(): List<EventGroup>
    fun getById(id: EventGroupId): EventGroup?
    fun insert(group: NewEventGroup): EventGroup?
}