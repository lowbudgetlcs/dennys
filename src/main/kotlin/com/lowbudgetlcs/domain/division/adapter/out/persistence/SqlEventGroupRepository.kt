package com.lowbudgetlcs.domain.division.adapter.out.persistence

import com.lowbudgetlcs.domain.division.core.model.EventGroup
import com.lowbudgetlcs.domain.division.core.model.NewEventGroup
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupName
import com.lowbudgetlcs.domain.division.core.model.types.toEventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.toEventGroupName
import com.lowbudgetlcs.domain.division.core.port.IEventGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENT_GROUPS

class SqlEventGroupRepository(
    private val dsl: DSLContext,
) : IEventGroupRepository {
    override suspend fun getAll(): List<EventGroup> =
        withContext(Dispatchers.IO) {
            select().fetch()
        }.mapNotNull(::rowToEventGroup)

    override suspend fun getById(id: EventGroupId): EventGroup? =
        withContext(Dispatchers.IO) {
            select().where(EVENT_GROUPS.ID.eq(id.value)).fetchOne()
        }?.let(::rowToEventGroup)

    override suspend fun insert(group: NewEventGroup): EventGroup? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl
                    .insertInto(EVENT_GROUPS)
                    .set(EVENT_GROUPS.NAME, group.name.value)
                    .returning(EVENT_GROUPS.ID)
                    .fetchOne()
            }
                ?.get(EVENT_GROUPS.ID)
        return insertedId?.toEventGroupId()?.let { getById(it) }
    }

    override suspend fun getByName(name: EventGroupName): EventGroup? =
        withContext(Dispatchers.IO) {
            select().where(EVENT_GROUPS.NAME.eq(name.value)).fetchOne()
        }?.let(::rowToEventGroup)

    override suspend fun update(update: EventGroup): EventGroup? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl
                    .update(EVENT_GROUPS)
                    .set(EVENT_GROUPS.NAME, update.name.value)
                    .where(EVENT_GROUPS.ID.eq(update.id.value))
                    .returning(EVENT_GROUPS.ID)
                    .fetchOne()
            }
                ?.get(EVENT_GROUPS.ID)
        return insertedId?.toEventGroupId()?.let { getById(it) }
    }

    private fun select() =
        dsl
            .select(
                EVENT_GROUPS.ID,
                EVENT_GROUPS.NAME,
            ).from(EVENT_GROUPS)

    private fun rowToEventGroup(row: Record): EventGroup? {
        val id = row[EVENT_GROUPS.ID]?.toEventGroupId() ?: return null
        val name = row[EVENT_GROUPS.NAME] ?: return null

        return EventGroup(
            id = id,
            name = name.toEventGroupName(),
        )
    }
}
