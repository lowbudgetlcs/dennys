package com.lowbudgetlcs.repositories.eventgroup

import com.lowbudgetlcs.domain.models.eventgroup.EventGroup
import com.lowbudgetlcs.domain.models.eventgroup.NewEventGroup
import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupId
import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupName

interface IEventGroupRepository {
    fun getAll(): List<EventGroup>

    fun getById(id: EventGroupId): EventGroup?

    fun insert(group: NewEventGroup): EventGroup?

    fun getByName(name: EventGroupName): EventGroup?

    fun update(update: EventGroup): EventGroup?
}
