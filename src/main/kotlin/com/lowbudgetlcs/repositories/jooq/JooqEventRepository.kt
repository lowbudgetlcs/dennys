package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.*
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
                id = EventId(it[EVENTS.ID]!!),
                name = it[EVENTS.NAME]!!,
                description = it[EVENTS.DESCRIPTION]!!,
                tournamentId = TournamentId(it[EVENTS.RIOT_TOURNAMENT_ID]!!),
                eventGroupId = it[EVENT_GROUPS.ID]?.let { id -> EventGroupId(id) },
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
                    id = EventId(it[EVENTS.ID]!!),
                    name = it[EVENTS.NAME]!!,
                    description = it[EVENTS.DESCRIPTION]!!,
                    tournamentId = TournamentId(it[EVENTS.RIOT_TOURNAMENT_ID]!!),
                    createdAt = it[EVENTS.CREATED_AT]!!,
                    startDate = it[EVENTS.START_DATE]!!,
                    endDate = it[EVENTS.END_DATE]!!,
                    eventGroupId = it[EVENTS.EVENT_GROUP_ID]?.let { id -> EventGroupId(id) },
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
                EVENTS.STATUS
            )
            .from(EVENTS)
            .where(EVENTS.ID.eq(id.value))
            .fetchOne()?.let {
                Event(
                    id = EventId(it[EVENTS.ID]!!),
                    name = it[EVENTS.NAME]!!,
                    description = it[EVENTS.DESCRIPTION]!!,
                    tournamentId = TournamentId(it[EVENTS.RIOT_TOURNAMENT_ID]!!),
                    createdAt = it[EVENTS.CREATED_AT]!!,
                    startDate = it[EVENTS.START_DATE]!!,
                    endDate = it[EVENTS.END_DATE]!!,
                    eventGroupId = it[EVENTS.EVENT_GROUP_ID]?.let { id -> EventGroupId(id) },
                    status = EventStatus.valueOf(it[EVENTS.STATUS]!!)
                )
            }

    override fun insert(event: NewEvent, tournamentId: TournamentId): Event? =
        dsl
            .insertInto(
                EVENTS
            )
            .set(EVENTS.NAME, event.name)
            .set(EVENTS.DESCRIPTION, event.description)
            .set(EVENTS.RIOT_TOURNAMENT_ID, tournamentId.value)
            .set(EVENTS.START_DATE, event.startDate)
            .set(EVENTS.END_DATE, event.endDate)
            .set(EVENTS.STATUS, event.status.name)
            .returning(EVENTS.ID, EVENTS.CREATED_AT)
            .fetchOne()?.let { row ->
                Event(
                    id = EventId(row[EVENTS.ID]!!),
                    name = event.name,
                    description = event.description,
                    tournamentId = tournamentId,
                    createdAt = row[EVENTS.CREATED_AT]!!,
                    startDate = event.startDate,
                    endDate = event.endDate,
                    eventGroupId = row[EVENTS.EVENT_GROUP_ID]?.let { id -> EventGroupId(id) },
                    status = event.status
                )
            }

    override fun updateStatusById(
        id: EventId,
        status: EventStatus
    ): Event? {
        TODO("Not yet implemented")
    }
}