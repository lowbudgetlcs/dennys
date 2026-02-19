package com.lowbudgetlcs.repositories.event

import com.lowbudgetlcs.domain.models.eventgroup.toEventGroupId
import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupId
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.EventUpdate
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.RiotTournamentId
import com.lowbudgetlcs.domain.models.events.Stage
import com.lowbudgetlcs.domain.models.events.patch
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.events.toRiotTournamentId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENTS

class EventRepository(
    private val dsl: DSLContext,
) : IEventRepository {
    override fun getAll(): List<Event> = selectEvents().fetch().mapNotNull(::rowToEvent)

    override fun getAllByGroupId(groupId: EventGroupId): List<Event> =
        selectEvents().where(EVENTS.EVENT_GROUP_ID.eq(groupId.value)).fetch().mapNotNull(::rowToEvent)

    override fun getById(id: EventId): Event? = selectEvents().where(EVENTS.ID.eq(id.value)).fetchOne(::rowToEvent)

    override fun getByName(name: String): Event? =
        selectEvents().where(EVENTS.NAME.eq(name)).fetchOne()?.let(::rowToEvent)

    override fun insert(
        newEvent: NewEvent,
        riotTournamentId: RiotTournamentId,
    ): Event? {
        val insertedId =
            dsl
                .insertInto(
                    EVENTS,
                ).set(EVENTS.NAME, newEvent.name)
                .set(EVENTS.DESCRIPTION, newEvent.description)
                .set(EVENTS.RIOT_TOURNAMENT_ID, riotTournamentId.value)
                .set(EVENTS.START_DATE, newEvent.startDate)
                .set(EVENTS.END_DATE, newEvent.endDate)
                .set(EVENTS.STATUS, newEvent.status.name)
                .set(EVENTS.STAGES, newEvent.stages.map { it.name }.toTypedArray())
                .returning(EVENTS.ID)
                .fetchOne()
                ?.get(EVENTS.ID)
        return insertedId?.toEventId()?.let(::getById)
    }

    override fun update(
        event: Event,
        update: EventUpdate,
    ): Event? {
        val patch = event.patch(update)
        val updatedId =
            dsl
                .update(EVENTS)
                .set(EVENTS.NAME, patch.name)
                .set(EVENTS.DESCRIPTION, patch.description)
                .set(EVENTS.START_DATE, patch.startDate)
                .set(EVENTS.END_DATE, patch.endDate)
                .set(EVENTS.STATUS, patch.status.name)
                .set(EVENTS.EVENT_GROUP_ID, patch.eventGroupId?.value)
                .where(EVENTS.ID.eq(event.id.value))
                .returning(EVENTS.ID)
                .fetchOne()
                ?.get(EVENTS.ID)
        return updatedId?.toEventId()?.let(::getById)
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
        val stages =
            row[EVENTS.STAGES]
                ?.filterNotNull()
                ?.mapNotNull { stageName ->
                    runCatching { Stage.valueOf(stageName) }.getOrNull()
                }?.toSet()
                ?: emptySet()
        return Event(
            id = eventId,
            name = name,
            description = description,
            riotTournamentId = tournamentId,
            createdAt = createdAt,
            startDate = startDate,
            endDate = endDate,
            eventGroupId = eventGroupId,
            status = status,
            stages = stages,
        )
    }
}
