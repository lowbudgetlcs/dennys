package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.repositories.IEventGroupRepository

class InMemoryEventGroupRepository : IEventGroupRepository {
    private val groups: MutableList<EventGroup> = mutableListOf()
    fun clear() {
        groups.clear()
    }

    override fun getAll(): List<EventGroup> = groups

    override fun getById(id: EventGroupId): EventGroup? = groups.find { it.id == id }
}
