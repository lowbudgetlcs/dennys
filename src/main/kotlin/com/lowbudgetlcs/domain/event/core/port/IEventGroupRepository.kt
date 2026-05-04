package com.lowbudgetlcs.domain.event.core.port

import com.lowbudgetlcs.domain.event.core.model.EventGroup
import com.lowbudgetlcs.domain.event.core.model.NewEventGroup
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName

interface IEventGroupRepository {
    suspend fun getAll(): List<EventGroup>
    suspend fun getById(id: EventGroupId): EventGroup?
    suspend fun insert(group: NewEventGroup): EventGroup?
    suspend fun getByName(name: EventGroupName): EventGroup?
    suspend fun update(update: EventGroup): EventGroup?
}
