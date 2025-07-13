package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.*
import org.jooq.DSLContext
import org.jooq.storage.tables.references.PLAYERS

class JooqPlayerRepository(private val dsl: DSLContext) {

    fun getAll(): List<Player> =
        dsl
            .select(
                PLAYERS.ID, PLAYERS.NAME, PLAYERS.EVENT_ID, PLAYERS.TEAM_ID
            ).from(PLAYERS).fetch().map { row ->
                Player(
                    id = PlayerId(row[PLAYERS.ID]!!),
                    name = PlayerName(row[PLAYERS.NAME]!!),
                    teamId = row[PLAYERS.TEAM_ID]?.let { TeamId(it) },
                    eventId = row[PLAYERS.EVENT_ID]?.let { EventId(it) }
                )
            }

}