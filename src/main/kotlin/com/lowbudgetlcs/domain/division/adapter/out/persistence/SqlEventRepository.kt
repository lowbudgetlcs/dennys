package com.lowbudgetlcs.domain.division.adapter.out.persistence

import com.lowbudgetlcs.domain.division.core.model.Event
import com.lowbudgetlcs.domain.division.core.model.EventUpdate
import com.lowbudgetlcs.domain.division.core.model.NewEvent
import com.lowbudgetlcs.domain.division.core.model.RiotTournamentId
import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.model.patch
import com.lowbudgetlcs.domain.division.core.model.toRiotTournamentId
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.division.core.model.types.EventName
import com.lowbudgetlcs.domain.division.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.division.core.model.types.toEventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.model.types.toEventName
import com.lowbudgetlcs.domain.division.core.port.IEventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENTS

class SqlEventRepository(
    private val dsl: DSLContext,
) : IEventRepository {
    override suspend fun getAll(): List<Event> = withContext(Dispatchers.IO) {
        selectEvents().fetch()
    }.mapNotNull(::rowToEvent)

    override suspend fun getAllByGroupId(groupId: EventGroupId): List<Event> =
        withContext(Dispatchers.IO) {
            selectEvents().where(EVENTS.EVENT_GROUP_ID.eq(groupId.value)).fetch()
        }.mapNotNull(::rowToEvent)

    override suspend fun getById(id: EventId): Event? =
        withContext(Dispatchers.IO) {
            selectEvents().where(EVENTS.ID.eq(id.value)).fetchOne(::rowToEvent)
        }

    override suspend fun getByName(name: EventName): Event? =
        withContext(Dispatchers.IO) {
            selectEvents().where(EVENTS.NAME.eq(name.value)).fetchOne()
        }?.let(::rowToEvent)

    override suspend fun insert(
        newEvent: NewEvent,
        riotTournamentId: RiotTournamentId,
    ): Event? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl
                    .insertInto(
                        EVENTS,
                    ).set(EVENTS.NAME, newEvent.name.value)
                    .set(EVENTS.DESCRIPTION, newEvent.description.value)
                    .set(EVENTS.RIOT_TOURNAMENT_ID, riotTournamentId.value)
                    .set(EVENTS.START_DATE, newEvent.startDate)
                    .set(EVENTS.END_DATE, newEvent.endDate)
                    .set(EVENTS.STATUS, newEvent.status.name)
                    .set(EVENTS.EVENT_GROUP_ID, newEvent.eventGroupId?.value)
                    .set(EVENTS.STAGES, newEvent.eventStages.map { it.name }.toTypedArray())
                    .returning(EVENTS.ID)
                    .fetchOne()
            }
                ?.get(EVENTS.ID)
        return insertedId?.toEventId()?.let { getById(it) }
    }

    override suspend fun update(
        event: Event,
        update: EventUpdate,
    ): Event? {
        val patch = event.patch(update)
        val updatedId =
            withContext(Dispatchers.IO) {
                dsl
                    .update(EVENTS)
                    .set(EVENTS.NAME, patch.name.value)
                    .set(EVENTS.DESCRIPTION, patch.description.value)
                    .set(EVENTS.START_DATE, patch.startDate)
                    .set(EVENTS.END_DATE, patch.endDate)
                    .set(EVENTS.STATUS, patch.status.name)
                    .set(EVENTS.EVENT_GROUP_ID, patch.eventGroupId?.value)
                    .where(EVENTS.ID.eq(event.id.value))
                    .returning(EVENTS.ID)
                    .fetchOne()
            }
                ?.get(EVENTS.ID)
        return updatedId?.toEventId()?.let { getById(it) }
    }

    private fun selectEvents() =
        dsl
            .select(
                EVENTS.ID,
                EVENTS.NAME,
                EVENTS.DESCRIPTION,
                EVENTS.RIOT_TOURNAMENT_ID,
                EVENTS.CREATED_AT,
                EVENTS.START_DATE,
                EVENTS.END_DATE,
                EVENTS.STATUS,
                EVENTS.EVENT_GROUP_ID,
                EVENTS.STAGES,
            ).from(EVENTS)

    fun rowToEvent(row: Record): Event? {
        val eventId = row[EVENTS.ID]?.toEventId() ?: return null
        val name = row[EVENTS.NAME] ?: return null
        val description = row[EVENTS.DESCRIPTION] ?: return null
        val tournamentId = row[EVENTS.RIOT_TOURNAMENT_ID]?.toRiotTournamentId() ?: return null
        val createdAt = row[EVENTS.CREATED_AT] ?: return null
        val startDate = row[EVENTS.START_DATE] ?: return null
        val endDate = row[EVENTS.END_DATE] ?: return null
        val status = row[EVENTS.STATUS]?.let { EventStatus.valueOf(it) } ?: return null
        val eventGroupId = row[EVENTS.EVENT_GROUP_ID]?.toEventGroupId()
        val eventStages =
            row[EVENTS.STAGES]
                ?.filterNotNull()
                ?.mapNotNull { stageName ->
                    runCatching { EventStage.valueOf(stageName) }.getOrNull()
                }?.toSet()
                ?: emptySet()
        return Event(
            id = eventId,
            name = name.toEventName(),
            description = description.toEventDescription(),
            riotTournamentId = tournamentId,
            createdAt = createdAt,
            startDate = startDate,
            endDate = endDate,
            eventGroupId = eventGroupId,
            status = status,
            eventStages = eventStages,
        )
    }
}
