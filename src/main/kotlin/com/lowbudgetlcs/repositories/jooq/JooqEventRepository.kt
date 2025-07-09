package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.event.Event
import com.lowbudgetlcs.domain.models.event.EventStatus
import com.lowbudgetlcs.domain.models.event.NewEvent
import org.jooq.DSLContext
import org.jooq.storage.tables.Events.Companion.EVENTS

class EventRepository(private val dsl: DSLContext) {

    fun findById(id: Int): Event? =
        dsl
            .select(
                EVENTS.ID,
                EVENTS.NAME,
                EVENTS.DESCRIPTION,
                EVENTS.RIOT_TOURNAMENT_ID,
                EVENTS.CREATED_AT,
                EVENTS.START_DATE,
                EVENTS.END_DATE,
                EVENTS.STATUS
            )
            .from(EVENTS)
            .where(EVENTS.ID.eq(id))
            .fetchOne()?.let { row ->
                Event(
                    id = row[EVENTS.ID]!!,
                    name = row[EVENTS.NAME]!!,
                    description = row[EVENTS.DESCRIPTION]!!,
                    riotTournamentId = row[EVENTS.RIOT_TOURNAMENT_ID]!!,
                    createdAt = row[EVENTS.CREATED_AT]!!,
                    startDate = row[EVENTS.START_DATE]!!,
                    endDate = row[EVENTS.END_DATE]!!,
                    status = EventStatus.valueOf(row[EVENTS.STATUS]!!)
                )
            }

    fun insert(event: NewEvent, riotTournamentId: Int): Event? =
        dsl
            .insertInto(
                EVENTS,
            )
            .set(EVENTS.NAME, event.name)
            .set(EVENTS.DESCRIPTION, event.description)
            .set(EVENTS.RIOT_TOURNAMENT_ID, riotTournamentId)
            .set(EVENTS.START_DATE, event.startDate)
            .set(EVENTS.END_DATE, event.endDate)
            .returning(EVENTS.ID, EVENTS.CREATED_AT)
            .fetchOne()?.let { row ->
                Event(
                    id = row[EVENTS.ID]!!,
                    name = event.name,
                    description = event.description,
                    riotTournamentId = riotTournamentId,
                    createdAt = row[EVENTS.CREATED_AT]!!,
                    startDate = event.startDate,
                    endDate = event.endDate,
                    status = event.status
                )
            }
}