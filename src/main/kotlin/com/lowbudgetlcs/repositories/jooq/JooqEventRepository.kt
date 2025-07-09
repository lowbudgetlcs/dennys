package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.event.Event
import com.lowbudgetlcs.domain.models.event.EventStatus
import com.lowbudgetlcs.domain.models.event.NewEvent
import org.jooq.DSLContext
import org.jooq.storage.tables.Events.Companion.EVENTS

class JooqEventRepository(private val dsl: DSLContext) {

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
            .fetchOne()?.let {
                Event(
                    id = it[EVENTS.ID]!!,
                    name = it[EVENTS.NAME]!!,
                    description = it[EVENTS.DESCRIPTION]!!,
                    riotTournamentId = it[EVENTS.RIOT_TOURNAMENT_ID]!!,
                    createdAt = it[EVENTS.CREATED_AT]!!,
                    startDate = it[EVENTS.START_DATE]!!,
                    endDate = it[EVENTS.END_DATE]!!,
                    status = EventStatus.valueOf(it[EVENTS.STATUS]!!)
                )
            }


    fun insert(event: NewEvent, riotTournamentId: Int): Event? =
        dsl
            .insertInto(
                EVENTS
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