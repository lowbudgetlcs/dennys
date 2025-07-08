package com.lowbudgetlcs.dto.events

import org.jooq.storage.tables.Events.Companion.EVENTS
import org.jooq.Record

fun Record.toEventDto(): EventDto = EventDto(
    id = this[EVENTS.ID]!!,
    name = this[EVENTS.NAME]!!,
    description = this[EVENTS.DESCRIPTION]!!,
    riotTournamentId = this[EVENTS.RIOT_TOURNAMENT_ID]!!,
    createdAt = this[EVENTS.CREATED_AT]!!,
    startDate = this[EVENTS.START_DATE]!!,
    endDate = this[EVENTS.END_DATE]!!,
    status = this[EVENTS.STATUS]!!
)