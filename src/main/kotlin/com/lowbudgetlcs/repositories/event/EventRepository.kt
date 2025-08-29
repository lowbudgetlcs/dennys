package com.lowbudgetlcs.repositories.event

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.toEventGroupId
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.storage.tables.references.EVENTS

class EventRepository(private val dsl: DSLContext) : IEventRepository {

    override fun getAll(): List<Event> = selectEvents().fetch().mapNotNull(::rowToEvent)

    override fun getAllByGroupId(groupId: EventGroupId): List<Event> =
        selectEvents().where(EVENTS.EVENT_GROUP_ID.eq(groupId.value)).fetch().mapNotNull(::rowToEvent)

    override fun getById(id: EventId): Event? =
        selectEvents().where(EVENTS.ID.eq(id.value)).fetchOne()?.let(::rowToEvent)

    override fun insert(newEvent: NewEvent, riotTournamentId: RiotTournamentId): Event? = try {
        val insertedId = dsl.insertInto(
            EVENTS
        ).set(EVENTS.NAME, newEvent.name).set(EVENTS.DESCRIPTION, newEvent.description)
            .set(EVENTS.RIOT_TOURNAMENT_ID, riotTournamentId.value).set(EVENTS.START_DATE, newEvent.startDate)
            .set(EVENTS.END_DATE, newEvent.endDate).set(EVENTS.STATUS, newEvent.status.name).returning(EVENTS.ID)
            .fetchOne()?.get(EVENTS.ID)
        insertedId?.toEventId()?.let(::getById)
    } catch (e: IntegrityConstraintViolationException) {
        null
    }

    override fun update(event: Event): Event? {
        val insertedId = dsl.update(EVENTS).set(EVENTS.NAME, event.name).set(EVENTS.DESCRIPTION, event.description)
            .set(EVENTS.START_DATE, event.startDate).set(EVENTS.END_DATE, event.endDate)
            .set(EVENTS.STATUS, event.status.name).where(EVENTS.ID.eq(event.id.value)).returning(EVENTS.ID).fetchOne()
            ?.get(EVENTS.ID)
        return insertedId?.toEventId()?.let(::getById)
    }

    private fun selectEvents() = dsl.select(
        EVENTS.ID,
        EVENTS.NAME,
        EVENTS.DESCRIPTION,
        EVENTS.RIOT_TOURNAMENT_ID,
        EVENTS.CREATED_AT,
        EVENTS.START_DATE,
        EVENTS.END_DATE,
        EVENTS.STATUS,
        EVENTS.EVENT_GROUP_ID
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
        return Event(
            id = eventId,
            name = name,
            description = description,
            riotTournamentId = tournamentId,
            createdAt = createdAt,
            startDate = startDate,
            endDate = endDate,
            eventGroupId = eventGroupId,
            status = status
        )
    }
}