package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.dto.events.CreateEventDto
import com.lowbudgetlcs.dto.events.EventDto
import com.lowbudgetlcs.dto.events.toEventDto
import org.jooq.DSLContext
import org.jooq.storage.tables.Events.Companion.EVENTS

class EventRepository(private val dsl: DSLContext) {

    fun create(entity: CreateEventDto): EventDto? =
        dsl
            .insertInto(
                EVENTS,
                EVENTS.NAME, EVENTS.DESCRIPTION, EVENTS.START_DATE, EVENTS.END_DATE, EVENTS.STATUS
            )
            .values(entity.name, entity.description, entity.startDate, entity.endDate, entity.status)
            .returning()
            .fetchOne()
            ?.toEventDto()


    fun getById(id: Int): EventDto? =
        dsl
            .select(EVENTS.asterisk())
            .from(EVENTS)
            .where(EVENTS.ID.eq(id))
            .fetchOne()
            ?.toEventDto()
}