package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.Team
import com.lowbudgetlcs.domain.models.TeamId
import com.lowbudgetlcs.domain.models.TeamName
import com.lowbudgetlcs.domain.models.events.EventId
import org.jooq.DSLContext
import org.jooq.storage.tables.Teams
import org.jooq.storage.tables.references.EVENTS
import org.jooq.storage.tables.references.TEAMS

class JooqTeamRepository(private val dsl: DSLContext) {

    fun getAll(): List<Team> {
        val result = dsl.select(
            TEAMS.ID,
            TEAMS.NAME,
            TEAMS.LOGO_NAME,
            EVENTS.ID,
            EVENTS.NAME,
            EVENTS.DESCRIPTION,
            EVENTS.CREATED_AT,
            EVENTS.START_DATE,
            EVENTS.END_DATE,
            EVENTS.STATUS
        )
            .from(Teams.Companion.TEAMS)
            .leftJoin(EVENTS).on(EVENTS.ID.eq(Teams.Companion.TEAMS.EVENT_ID))
            .fetch()
        return result.map { row ->
            Team(
                id = TeamId(row[TEAMS.ID]!!),
                name = TeamName(row[TEAMS.NAME]!!),
                logoName = row[TEAMS.LOGO_NAME],
                eventId = row[TEAMS.EVENT_ID]?.let { EventId(it) }
            )
        }
    }
}