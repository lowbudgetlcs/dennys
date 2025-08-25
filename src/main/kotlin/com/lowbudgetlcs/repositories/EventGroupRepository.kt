package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.events.EventGroup
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.domain.models.events.NewEventGroup
import com.lowbudgetlcs.domain.models.events.toEventGroupId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENT_GROUPS

class EventGroupRepository(private val dsl: DSLContext) : IEventGroupRepository {
    override fun getAll(): List<EventGroup> = select().fetch().mapNotNull(::rowToEventGroup)


    override fun getById(id: EventGroupId): EventGroup? =
        select().where(EVENT_GROUPS.ID.eq(id.value)).fetchOne()?.let(::rowToEventGroup)

    override fun insert(group: NewEventGroup): EventGroup? {
        val insertedId =
            dsl.insertInto(EVENT_GROUPS).set(EVENT_GROUPS.NAME, group.name).returning(EVENT_GROUPS.ID).fetchOne()
                ?.get(EVENT_GROUPS.ID)
        return insertedId?.toEventGroupId()?.let(::getById)
    }

    private fun select() = dsl.select(
        EVENT_GROUPS.ID, EVENT_GROUPS.NAME
    )

    private fun rowToEventGroup(row: Record): EventGroup? {
        val id = row[EVENT_GROUPS.ID]?.toEventGroupId() ?: return null
        val name = row[EVENT_GROUPS.NAME] ?: return null

        return EventGroup(
            id = id,
            name = name
        )
    }
}