package com.lowbudgetlcs.repositories.eventgroup

import com.lowbudgetlcs.domain.eventgroup.models.EventGroup
import com.lowbudgetlcs.domain.eventgroup.models.NewEventGroup
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

interface IEventGroupRepository {
    fun getAll(): List<EventGroup>

    fun getById(id: EventGroupId): EventGroup?

    fun insert(group: NewEventGroup): EventGroup?

    fun getByName(name: EventGroupName): EventGroup?

    fun update(update: EventGroup): EventGroup?
}
