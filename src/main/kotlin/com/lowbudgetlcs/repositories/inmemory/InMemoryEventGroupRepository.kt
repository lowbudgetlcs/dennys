package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.repositories.IEventGroupRepository

class InMemoryEventGroupRepository : IEventGroupRepository {
    private val groups: MutableList<EventGroup> = mutableListOf()
    fun clear() {
        groups.clear()
    }

    override fun getAll(): List<EventGroup> = groups

    override fun getById(id: EventGroupId): EventGroup? = groups.find { it.id == id }
    override fun insert(group: NewEventGroup): EventGroup? {
        val id = groups.size.toEventGroupId()
        val g = group.toEventGroup(id)
        groups.add(id.value, g)
        return g
    }
}
