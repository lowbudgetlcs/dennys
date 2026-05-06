package com.lowbudgetlcs.domain.division.core.event.port

import com.lowbudgetlcs.domain.division.core.event.model.EventGroup
import com.lowbudgetlcs.domain.division.core.event.model.NewEventGroup
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupName

interface IEventGroupRepository {
    suspend fun getAll(): List<EventGroup>
    suspend fun getById(id: EventGroupId): EventGroup?
    suspend fun insert(group: NewEventGroup): EventGroup?
    suspend fun getByName(name: EventGroupName): EventGroup?
    suspend fun update(update: EventGroup): EventGroup?
}
