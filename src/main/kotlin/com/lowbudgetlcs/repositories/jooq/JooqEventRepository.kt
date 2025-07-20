package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.tournament.TournamentId
import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.repositories.IEventRepository
import org.jooq.DSLContext
import org.jooq.storage.tables.references.EVENTS
import org.jooq.storage.tables.references.EVENT_GROUPS

class JooqEventRepository(private val dsl: DSLContext) : IEventRepository {

    override fun getAll(): List<Event> = dsl
        .select(
            EVENTS.ID,
            EVENTS.NAME,
            EVENTS.DESCRIPTION,
            EVENTS.RIOT_TOURNAMENT_ID,
            EVENTS.CREATED_AT,
            EVENTS.START_DATE,
            EVENTS.END_DATE,
            EVENTS.STATUS,
            EVENT_GROUPS.ID
        )
        .from(EVENTS)
        .fetch().map {
            Event(
                id = it[EVENTS.ID]!!.toEventId(),
                name = it[EVENTS.NAME]!!,
                description = it[EVENTS.DESCRIPTION]!!,
                tournamentId = it[EVENTS.RIOT_TOURNAMENT_ID]!!.toTournamentId(),
                eventGroupId = it[EVENT_GROUPS.ID]?.toEventGroupId(),
                createdAt = it[EVENTS.CREATED_AT]!!,
                startDate = it[EVENTS.START_DATE]!!,
                endDate = it[EVENTS.END_DATE]!!,
                status = EventStatus.valueOf(it[EVENTS.STATUS]!!)
            )
        }

    override fun getAllByGroupId(groupId: EventGroupId): List<Event> =
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
            .where(EVENTS.EVENT_GROUP_ID.eq(groupId.value))
            .fetch().map {
                Event(
                    id = it[EVENTS.ID]!!.toEventId(),
                    name = it[EVENTS.NAME]!!,
                    description = it[EVENTS.DESCRIPTION]!!,
                    tournamentId = it[EVENTS.RIOT_TOURNAMENT_ID]!!.toTournamentId(),
                    createdAt = it[EVENTS.CREATED_AT]!!,
                    startDate = it[EVENTS.START_DATE]!!,
                    endDate = it[EVENTS.END_DATE]!!,
                    eventGroupId = it[EVENTS.EVENT_GROUP_ID]?.toEventGroupId(),
                    status = EventStatus.valueOf(it[EVENTS.STATUS]!!)
                )
            }

    override fun getById(id: EventId): Event? =
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
                EVENTS.EVENT_GROUP_ID
            )
            .from(EVENTS)
            .where(EVENTS.ID.eq(id.value))
            .fetchOne()?.let {
                Event(
                    id = it[EVENTS.ID]!!.toEventId(),
                    name = it[EVENTS.NAME]!!,
                    description = it[EVENTS.DESCRIPTION]!!,
                    tournamentId = it[EVENTS.RIOT_TOURNAMENT_ID]!!.toTournamentId(),
                    createdAt = it[EVENTS.CREATED_AT]!!,
                    startDate = it[EVENTS.START_DATE]!!,
                    endDate = it[EVENTS.END_DATE]!!,
                    eventGroupId = it[EVENTS.EVENT_GROUP_ID]?.toEventGroupId(),
                    status = EventStatus.valueOf(it[EVENTS.STATUS]!!)
                )
            }

    override fun insert(newEvent: NewEvent, tournamentId: TournamentId): Event? =
        dsl
            .insertInto(
                EVENTS
            )
            .set(EVENTS.NAME, newEvent.name)
            .set(EVENTS.DESCRIPTION, newEvent.description)
            .set(EVENTS.RIOT_TOURNAMENT_ID, tournamentId.value)
            .set(EVENTS.START_DATE, newEvent.startDate)
            .set(EVENTS.END_DATE, newEvent.endDate)
            .set(EVENTS.STATUS, newEvent.status.name)
            .returning(EVENTS.ID, EVENTS.CREATED_AT)
            .fetchOne()?.let { row ->
                Event(
                    id = row[EVENTS.ID]!!.toEventId(),
                    name = newEvent.name,
                    description = newEvent.description,
                    tournamentId = tournamentId,
                    createdAt = row[EVENTS.CREATED_AT]!!,
                    startDate = newEvent.startDate,
                    endDate = newEvent.endDate,
                    eventGroupId = row[EVENTS.EVENT_GROUP_ID]?.toEventGroupId(),
                    status = newEvent.status
                )
            }
}