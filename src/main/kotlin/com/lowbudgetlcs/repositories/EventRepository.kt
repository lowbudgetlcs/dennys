package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.TournamentId
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.EVENTS

class EventRepository(private val dsl: DSLContext) : IEventRepository {

    override fun getAll(): List<Event> = selectEvents().fetch().mapNotNull(::rowToEvent)

    override fun getAllByGroupId(groupId: EventGroupId): List<Event> =
        selectEvents().where(EVENTS.EVENT_GROUP_ID.eq(groupId.value)).fetch().mapNotNull(::rowToEvent)

    override fun getById(id: EventId): Event? =
        selectEvents().where(EVENTS.ID.eq(id.value)).fetchOne()?.let(::rowToEvent)

    override fun insert(newEvent: NewEvent, tournamentId: TournamentId): Event? {
        val insertedId = dsl.insertInto(
            EVENTS
        ).set(EVENTS.NAME, newEvent.name).set(EVENTS.DESCRIPTION, newEvent.description)
            .set(EVENTS.RIOT_TOURNAMENT_ID, tournamentId.value).set(EVENTS.START_DATE, newEvent.startDate)
            .set(EVENTS.END_DATE, newEvent.endDate).set(EVENTS.STATUS, newEvent.status.name).returning(EVENTS.ID)
            .fetchOne()?.get(EVENTS.ID)
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
        val tournamentId = row[EVENTS.RIOT_TOURNAMENT_ID]?.toTournamentId() ?: return null
        val createdAt = row[EVENTS.CREATED_AT] ?: return null
        val startDate = row[EVENTS.START_DATE] ?: return null
        val endDate = row[EVENTS.END_DATE] ?: return null
        val status = row[EVENTS.STATUS]?.let { EventStatus.valueOf(it) } ?: return null
        val eventGroupId = row[EVENTS.EVENT_GROUP_ID]?.toEventGroupId()
        return Event(
            id = eventId,
            name = name,
            description = description,
            tournamentId = tournamentId,
            createdAt = createdAt,
            startDate = startDate,
            endDate = endDate,
            eventGroupId = eventGroupId,
            status = status
        )
    }
}