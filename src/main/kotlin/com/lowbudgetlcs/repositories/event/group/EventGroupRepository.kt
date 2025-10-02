package com.lowbudgetlcs.repositories.event.group

import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupName
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.domain.models.events.group.toEventGroupId
import com.lowbudgetlcs.domain.models.events.group.toEventGroupName
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENT_GROUPS

class EventGroupRepository(
    private val dsl: DSLContext,
) : IEventGroupRepository {
    override fun getAll(): List<EventGroup> = select().fetch().mapNotNull(::rowToEventGroup)

    override fun getById(id: EventGroupId): EventGroup? =
        select().where(EVENT_GROUPS.ID.eq(id.value)).fetchOne()?.let(::rowToEventGroup)

    override fun insert(group: NewEventGroup): EventGroup? {
        val insertedId =
            dsl
                .insertInto(EVENT_GROUPS)
                .set(EVENT_GROUPS.NAME, group.name.value)
                .returning(EVENT_GROUPS.ID)
                .fetchOne()
                ?.get(EVENT_GROUPS.ID)
        return insertedId?.toEventGroupId()?.let(::getById)
    }

    override fun getByName(name: EventGroupName): EventGroup? {
        TODO("Not yet implemented")
    }

    override fun update(update: EventGroup): EventGroup? {
        TODO("Not yet implemented")
    }

    private fun select() =
        dsl.select(
            EVENT_GROUPS.ID,
            EVENT_GROUPS.NAME,
        )

    private fun rowToEventGroup(row: Record): EventGroup? {
        val id = row[EVENT_GROUPS.ID]?.toEventGroupId() ?: return null
        val name = row[EVENT_GROUPS.NAME] ?: return null

        return EventGroup(
            id = id,
            name = name.toEventGroupName(),
        )
    }
}
