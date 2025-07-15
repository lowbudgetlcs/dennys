package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.EventGroup
import com.lowbudgetlcs.repositories.IEventGroupRepository

class InMemoryEventGroupRepository : IEventGroupRepository {
    override fun getAll(): List<EventGroup> {
        TODO("Not yet implemented")
    }
}
