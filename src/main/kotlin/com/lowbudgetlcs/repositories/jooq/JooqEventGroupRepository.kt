package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.repositories.IEventGroupRepository
import org.jooq.DSLContext

class JooqEventGroupRepository(dsl: DSLContext) : IEventGroupRepository {
    override fun getAll(): List<EventGroup> = TODO()

    override fun getById(id: EventGroupId): EventGroup? {
        TODO("Not yet implemented")
    }
}