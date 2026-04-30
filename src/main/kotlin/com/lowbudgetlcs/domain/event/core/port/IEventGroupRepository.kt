package com.lowbudgetlcs.domain.event.core.port

import com.lowbudgetlcs.domain.event.core.model.EventGroup
import com.lowbudgetlcs.domain.event.core.model.NewEventGroup
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName

interface IEventGroupRepository {
    fun getAll(): List<EventGroup>

    fun getById(id: EventGroupId): EventGroup?

    fun insert(group: NewEventGroup): EventGroup?

    fun getByName(name: EventGroupName): EventGroup?

    fun update(update: EventGroup): EventGroup?
}
