package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.EventGroup
import com.lowbudgetlcs.repositories.IEventGroupRepository
import org.jooq.DSLContext

class JooqEventGroupRepository(dsl: DSLContext) : IEventGroupRepository {
    override fun getAll(): List<EventGroup> {
        TODO("Not yet implemented")
    }
}